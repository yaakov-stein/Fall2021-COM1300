package edu.yu.cs.intro.doomGame;

import java.util.*;

/**
 * Plays through a given game scenario. i.e. tries to kill all the monsters in all the rooms and thus complete the game, using the given set of players
 */
public class GameBot {
    private SortedSet<Room> rooms;
    private SortedSet<Player> players;
    /**
     * Create a new "GameBot", i.e. a program that automatically "plays the game"
     * @param rooms the set of rooms in this game
     * @param players the set of players the bot can use to try to complete all rooms
     */
    public GameBot(SortedSet<Room> rooms, SortedSet<Player> players) {
        this.rooms = rooms;
        this.players = players;
    }

    /**
     * Try to complete killing all monsters in all rooms using the given set of players.
     * It could take multiple passes through the set of rooms to complete the task of killing every monster in every room.
     * This method should call #passThroughRooms in some loop that tracks whether all the rooms have been completed OR we
     * have reached a point at which no progress can be made. If we are "stuck", i.e. we haven't completed all rooms but
     * calls to #passThroughRooms are no longer increasing the number of rooms that have been completed, return false to
     * indicate that we can't complete the game. As long as the number of completed rooms continues to rise, keep calling
     * #passThroughRooms.
     *
     * Throughout our attempt/logic to play the game, we rely on and take advantage of the fact that Room, Monster,
     * and Player all implement Comparable, and the sets we work with are all SortedSets
     *
     * @return true if all rooms were completed, false if not
     */
    public boolean play() {
        if(this.players == null || this.players.isEmpty()){
            throw new IllegalArgumentException("No existing players");
        }
        int passedRooms = 0;
        int allRooms = this.rooms.size();
        int progressMade = 0;
        for(int i = 0; (passedRooms != allRooms) && (progressMade < (i + 10)); i++){
            Set<Room> passedRoomSet = this.passThroughRooms();
            progressMade = passedRoomSet.isEmpty() ? progressMade + 2:i;
            passedRooms += passedRoomSet.size();
        }
        return passedRooms == allRooms;
    }

    /**
     * Pass through the rooms, killing any monsters that can be killed, and thus attempt to complete the rooms
     * @return the set of rooms that were completed in this pass
     */
    protected Set<Room> passThroughRooms() {
        Set<Room> completedRooms = new HashSet<>();
        for(Room a:this.getAllRooms()) {
            SortedSet<Monster> copyOfLiveMonsters = new TreeSet<>(a.getLiveMonsters());
            if (a.isCompleted()) {
                continue;
            }
            Iterator<Player> b;
            for (b = this.getLivePlayers().iterator(); b.hasNext(); ){
            //for (Player activePlayer : this.getLivePlayers()) {
                Player activePlayer = b.next();
                if(a.isCompleted()){
                    continue;
                }
                for (Monster c : copyOfLiveMonsters) {
                    if(c.isDead()){
                        continue;
                    }
                    if (canKill(activePlayer, c, a)) {
                        this.killMonster(activePlayer, a, c);
                        if (a.isCompleted()) {
                            this.reapCompletionRewards(activePlayer, a);
                            completedRooms.add(a);
                        }
                        b = this.getLivePlayers().iterator();
                        break;
                    }
                }
            }
        }
        return completedRooms;
        //for every room that is not completed,
            //for every living monster in that room
                //See if any of your players can kill the monster. If so, have the capable player kill it.
                //The player that causes the room to be completed by killing a monster reaps the rewards for completing that room.
        //Return the set of completed rooms.
    }

    /**
     * Have the given player kill the given monster in the given room.
     * Assume that #canKill was already called to confirm that player's ability to kill the monster
     * @param player
     * @param room
     * @param monsterToKill
     */
    protected void killMonster(Player player, Room room, Monster monsterToKill) {
        SortedSet<Monster> protectors = getAllProtectorsInRoom(monsterToKill,room);
        Set<Monster> immediateProtectors = new HashSet<>();
        int roomHealthLost = room.getPlayerHealthLostPerEncounter();
        for(Monster a : protectors){
            this.killMonster(player, room, a);
            if(a.getMonsterType() == monsterToKill.getProtectedBy()){
                immediateProtectors.add(a);
            }
        }
        /*for(Monster a : immediateProtectors){
            this.killMonster(player, room, a);
        }*/
        if(monsterToKill.getMonsterType().weaponNeededToKill != Weapon.FIST){
            int ammoNeededToKill = monsterToKill.getMonsterType().ammunitionCountNeededToKill;
            int validAmmoAttained = 0;
            SortedSet<Weapon> playerWeaponSet = (SortedSet<Weapon>)player.getWeaponSet();
            for(Weapon a: playerWeaponSet){
                if(monsterToKill.getMonsterType().weaponNeededToKill.ordinal() >= a.ordinal() && a != Weapon.FIST){
                    int availableAmmo = player.getAmmunitionRoundsForWeapon(a);
                    validAmmoAttained += availableAmmo;
                    int ammoUsed;
                    if(validAmmoAttained >= ammoNeededToKill){
                        ammoUsed = ammoNeededToKill - (validAmmoAttained - availableAmmo);
                        if(player.getAmmunitionRoundsForWeapon(a) < ammoUsed){
                            throw new IllegalArgumentException("Not Enough Ammo");
                        }
                        player.changeAmmunitionRoundsForWeapon(a,-ammoUsed);
                    }else{
                        if(player.getAmmunitionRoundsForWeapon(a) < availableAmmo){
                            throw new IllegalArgumentException("Not Enough Ammo");
                        }
                        player.changeAmmunitionRoundsForWeapon(a,-availableAmmo);
                    }
                }
            }
        }
        if(!(player.getHealth() - roomHealthLost > 0)){
            throw new IllegalArgumentException("Not Enough Health");
        }
        player.changeHealth(-roomHealthLost);
        if(player.getHealth() <= 0){
            throw new IllegalArgumentException("Player is dead");
        }
        room.monsterKilled(monsterToKill);
        monsterToKill.setDead();
        System.out.println("Room: " + room.getName() + ". Player " + player.getName() + " has killed a " + monsterToKill.getMonsterType() + ". Health is now " + player.getHealth());
        //Call getAllProtectorsInRoom to get a sorted set of all the monster's protectors in this room
        //Player must kill the protectors before it can kill the monster, so kill all the protectors
        //first via a recursive call to killMonster on each one.
        //Reduce the player's health by the amount given by room.getPlayerHealthLostPerEncounter().
        //Attack (and thus kill) the monster with the kind of weapon, and amount of ammunition, needed to kill it.
    }

    /**
     * give the player the weapons, ammunition, and health that come from completing the given room
     * @param player
     * @param room
     */
    protected void reapCompletionRewards(Player player, Room room) {
        player.changeHealth(room.getHealthWonUponCompletion());
        for(Weapon a:room.getWeaponsWonUponCompletion()){
            player.addWeapon(a);
        }
        Map<Weapon,Integer> ammoWon = room.getAmmoWonUponCompletion();
        for(Weapon a:ammoWon.keySet()){
            int ammoInt = ammoWon.get(a);
            player.addAmmunition(a,ammoInt);
        }
    }

    /**
     * @return a set of all the rooms that have been completed
     */
    public Set<Room> getCompletedRooms() {
        Set<Room> completedRooms = new HashSet<>();
        for(Room a:this.rooms){
            if(a.isCompleted()){
                completedRooms.add(a);
            }
        }
        return completedRooms;
    }

    /**
     * @return an unmodifiable collection of all the rooms in the came
     * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
     */
    public SortedSet<Room> getAllRooms() {
        return Collections.unmodifiableSortedSet(this.rooms);
    }

    /**
     * @return a sorted set of all the live players in the game
     */
    protected SortedSet<Player> getLivePlayers() {
        SortedSet<Player> livePlayers = new TreeSet<>();
        for(Player a:this.players){
            if(!a.isDead()){
                livePlayers.add(a);
            }
        }
        return livePlayers;
    }

    /**
     * @param weapon
     * @param ammunition
     * @return a sorted set of all the players that have the given weapon with the given amount of ammunition for it
     */
    protected SortedSet<Player> getLivePlayersWithWeaponAndAmmunition(Weapon weapon, int ammunition) {
        //may need to change to include not only given weapon but also any weapon of a higher ordinal
        SortedSet<Player> readyPlayers = new TreeSet<>();
        for(Player a:this.getLivePlayers()){
            if(a.hasWeapon(weapon) && (a.getAmmunitionRoundsForWeapon(weapon) >= ammunition)){
                readyPlayers.add(a);
            }
        }
        return readyPlayers;
    }

    /**
     * Get the set of all monsters that would need to be killed first before you could kill this one.
     * Remember that a protector may itself be protected by other monsters, so you will have to recursively check for protectors
     * @param monster
     * @param room
     * @return
     */
    protected static SortedSet<Monster> getAllProtectorsInRoom(Monster monster, Room room) {
        return getAllProtectorsInRoom(new TreeSet<>(), monster, room); //this is a hint about how to handle canKill as well
    }

    private static SortedSet<Monster> getAllProtectorsInRoom(SortedSet<Monster> protectors, Monster monster, Room room) {
        SortedSet<Monster> liveMonsters = new TreeSet<>(room.getLiveMonsters());
        MonsterType protector = monster.getProtectedBy();
        if(protector != null){
            for(Monster a:liveMonsters){
                if(a.getMonsterType() == protector){
                    protectors.add(a);
                    getAllProtectorsInRoom(protectors,a,room);
                }
            }
        }
        return protectors;
    }

    /**
     * Can the given player kill the given monster in the given room?
     *
     * @param player
     * @param monster
     * @param room
     * @return
     * @throws IllegalArgumentException if the monster is not located in the room or is dead
     */
    protected static boolean canKill(Player player, Monster monster, Room room) {
        //Going into the room exposes the player to all the monsters in the room. If the player's health is
        //not > room.getPlayerHealthLostPerEncounter(), you can return immediately.
        //Call the private canKill method, to determine if this player can kill this monster.
        //Before returning from this method, reset the player's health to what it was before you called the private canKill
        if(monster.isDead()){
            throw new IllegalArgumentException("Monster is Dead");
        }
        boolean exists = false;
        for(Monster a:room.getLiveMonsters()){
            if(monster.compareTo(a) == 0){
                exists = true;
            }
        }
        if(!exists){
            /*for(Monster a:room.getLiveMonsters()){
                System.out.println(monster.getMonsterType() + " hashcode is " + monster.hashCode() + " and " + a.getMonsterType() + " is " + monster.hashCode());
            }
            for(Monster a: room.getDeadMonsters()) {
                System.out.println(a.getMonsterType());
            }
            System.out.println("\n");
            for(Monster a:room.getLiveMonsters()){
                System.out.println(a.getMonsterType());

            }*/
            throw new IllegalArgumentException("Monster " + monster.getMonsterType() + " is not in room " + room.getName());
        }
        if(player.isDead()){
            throw new IllegalArgumentException("Player is dead");
        }
        int playerStartHealth = player.getHealth();
        SortedSet<Monster> roomStartingMonsters = new TreeSet<>(room.getLiveMonsters());
        if(playerStartHealth < room.getPlayerHealthLostPerEncounter()){
            return false;
        }
        boolean canKill = canKill(player, monster, room, new TreeMap<>(), new HashSet<>());
        player.setHealth(playerStartHealth);
        for(Monster a:roomStartingMonsters){
            room.addMonster(a);
        }
        return canKill;
        /*
         * 1. check that player, monster, room area not null
         * 2. Check that player and monster are alive
         * 3. Check that the monster is in the room
         * 4. get Player's health and set to variable
         * 5. Get a copy of all liveMonsters in room
         * 6. Check that players health is greater than room.getPlayerHealthLostPerEncounter
         * 7. if canKill returns true, set boolean to true
         * 8. No matter what, reset players health and monsters in room to previous conditions
         * 9. Return boolean
         */
    }

    /**
     *
     * @param player
     * @param monster
     * @param room
     * @param roundsUsedPerWeapon
     * @return
     */
    private static boolean canKill(Player player, Monster monster, Room room, SortedMap<Weapon,Integer> roundsUsedPerWeapon, Set<Monster>
            alreadyMarkedByCanKill) {
        //Remove all the monsters already marked / looked at by this series of recursive calls to canKill from the set of liveMonsters
        // in the room before you check if the monster is alive and in the room. Be sure to NOT alter the actual set of live monsters
        // in your Room object!
        //Check if monster is in the room and alive.
        //Check what weapon is needed to kill the monster, see if player has it. If not, return false.
        //Check what protects the monster. If the monster is protected, the player can only kill this monster if it can kill its protectors as well.
        //Make recursive calls to canKill to see if player can kill its protectors.
        //Be sure to remove all members of alreadyMarkedByCanKill from the set of protectors before you recursively call canKill on the protectors.
        //If all the recursive calls to canKill on all the protectors returned true:
        //Check what amount of ammunition is needed to kill the monster, and see if player has it after we subtract
        //from his total ammunition the number stored in roundsUsedPerWeapon for the given weapon, if any.
        //add how much ammunition will be used up to kill this monster to roundsUsedPerWeapon
        //Add up the playerHealthLostPerExposure of all the live monsters, and see if when that is subtracted from the player if his health
        // is still > 0. If not, return false.
        //If health is still > 0, subtract the above total from the player's health
        //(Note that in the protected canKill method, you must reset the player's health to what it was before canKill was called before
        // you return from that protected method)
        //add this monster to alreadyMarkedByCanKill, and return true.
        Weapon weaponNeeded = monster.getMonsterType().weaponNeededToKill;
        int weaponOrdinal = weaponNeeded.ordinal();
        int roomHealthLost = room.getPlayerHealthLostPerEncounter();
        boolean hasValidWeapon = false;
        SortedSet<Weapon> playerWeaponSet = (SortedSet<Weapon>) player.getWeaponSet();
        for(Weapon a: playerWeaponSet){
            if(weaponOrdinal >= a.ordinal()){
                hasValidWeapon = true;
                break;
            }
        }
        if(!hasValidWeapon){
            return false;
        }
        SortedSet<Monster> protectors = getAllProtectorsInRoom(monster,room);
        Set<Monster> immediateProtectors = new HashSet<>();
        for(Monster a: protectors){
            if(a.getMonsterType() == monster.getProtectedBy()){
                immediateProtectors.add(a);
            }
        }
        for(Monster a: immediateProtectors){
            if(!canKill(player,a,room,roundsUsedPerWeapon,alreadyMarkedByCanKill)){
                return false;
            }
        }
        if(weaponNeeded != Weapon.FIST){
            int ammoNeededToKill = monster.getMonsterType().ammunitionCountNeededToKill;
            int validAmmoAttained = 0;
            boolean enoughAmmo = false;
            for(Weapon a:playerWeaponSet){
                if(weaponOrdinal <= a.ordinal() && a != Weapon.FIST){
                    if(!roundsUsedPerWeapon.containsKey(a)){
                        roundsUsedPerWeapon.put(a,0);
                    }
                    int availableAmmo = player.getAmmunitionRoundsForWeapon(a) - roundsUsedPerWeapon.get(a);
                    validAmmoAttained += availableAmmo;
                    if(validAmmoAttained >= ammoNeededToKill){
                        int ammoUsed = ammoNeededToKill - (validAmmoAttained - availableAmmo);
                        roundsUsedPerWeapon.replace(a,roundsUsedPerWeapon.get(a) + ammoUsed);
                        enoughAmmo = true;
                        break;
                    }else{
                        roundsUsedPerWeapon.replace(a,roundsUsedPerWeapon.get(a) + availableAmmo);
                    }
                }
            }
            if(!enoughAmmo){
                return false;
            }
        }
        if(!(player.getHealth() - roomHealthLost > 0)){
            return false;
        }
        player.changeHealth(-roomHealthLost);
        room.monsterKilled(monster);
        return true;
        /*
        *
        * 2. Check Weapon needed to kill monster.
        *   2a. Check if Player has that weapon or any weapon with a higher ordinal (every player has fist)
        *       2aa. Create variable that saves weapon type that will be used to kill monster
        * 3. Check if the monster is protected. If so, call getAllProtectorsInRoom to get all monsters that protect this monster
        * 4. Get set of all monsters that are MonsterType of this monsters .getProtectedBy() call immediateProtectors
        * 5. Using a for each loop, iterate through the set of immediateProtectors
        *   5a. if canKill returns true on current object of immediateProtectors, continue, if returns false, return false
        * 6. if it makes it through for each loop,
        *   6a. Check if player has necessary weapon and ammo, or a higher ordinal weapon with enough ammo (can use variable created in 3aa
        *       as a starting point to check if has valid weapon) and do not need to check if weapon is fist. Also, first use up all low-level
        *       ammo before going to ammo from a higher ordinal weapon
        *   6b. Check if player has necessary health to kill the monster
        * 7. Subtract Health lost from health
        * 8. Add ammo being used to roundsUsedPerWeaponSet(not necessary for fist)
        * 9. kill monster via monsterKilled
        * 10. Return True
        * */
    }
}

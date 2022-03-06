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
    }
}

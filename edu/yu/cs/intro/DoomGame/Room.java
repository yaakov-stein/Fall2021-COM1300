package edu.yu.cs.intro.doomGame;

import java.util.*;

/**
 * A Room in the game, which contains both monsters as well as rewards for the player that completes the room,
 * which is defined as the player who kills the last living monster in the room
 */
public class Room implements Comparable<Room>{
    private SortedSet<Monster> allMonsters;
    private SortedSet<Monster> liveMonsters;
    private SortedSet<Monster> deadMonsters = new TreeSet<>();
    private final Set<Weapon> weaponsWonUponCompletion;
    private final Map<Weapon,Integer> ammoWonUponCompletion;
    private final String name;
    private final int healthWonUponCompletion;
    /**
     *
     * @param monsters the monsters present in this room. This is a set of Monsters, NOT MonsterTypes - can have multiple monsters of a given type in a room
     * @param weaponsWonUponCompletion weapons a player gains when killing the last monster in this room
     * @param ammoWonUponCompletion ammunition a player gains when killing the last monster in this room
     * @param healthWonUponCompletion health a player gains when killing the last monster in this room
     * @param name the room's name
     */
    public Room(SortedSet<Monster> monsters, Set<Weapon> weaponsWonUponCompletion,Map<Weapon,Integer> ammoWonUponCompletion,int healthWonUponCompletion,String name){
        this.allMonsters = monsters;
        this.liveMonsters = new TreeSet<>(monsters);
        this.weaponsWonUponCompletion = weaponsWonUponCompletion;
        this.ammoWonUponCompletion = ammoWonUponCompletion;
        this.healthWonUponCompletion = healthWonUponCompletion;
        this.name = name;
    }

    /**
     * Mark the given monster as being dead.
     * Reduce the danger level of this room by monster.getMonsterType().ordinal()+1
     * @param monster
     */
    protected void monsterKilled(Monster monster){
        this.liveMonsters.remove(monster);
        this.deadMonsters.add(monster);
    }
    protected boolean addMonster(Monster monster){
        this.deadMonsters.remove(monster);
        return this.liveMonsters.add(monster);
    }

    /**
     * The danger level of the room is defined as the sum of the ordinal+1 value of all living monsters, i.e. adding up (m.getMonsterType().ordinal() + 1) of all the living monsters
     * @return the danger level of this room
     */
    public int getDangerLevel(){
        int totalDangerLevel = 0;
        for(Monster a:this.liveMonsters){
            totalDangerLevel += (a.getMonsterType().ordinal() + 1);
        }
        return totalDangerLevel;
    }

    /**
     * @return name of this room
     */
    public String getName(){
        return this.name;
    }

    /**
     * compares based on danger level
     * @param other
     * @return
     */
    @Override
    public int compareTo(Room other) {
        return this.getDangerLevel() - other.getDangerLevel();
    }

    /**
     * @return the set of weapons the player who completes the room is rewarded with. Make sure you don't allow the caller to modify the actual set!
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public Set<Weapon> getWeaponsWonUponCompletion(){
        return Collections.unmodifiableSet(this.weaponsWonUponCompletion);
    }

    /**
     * @return The set of ammunition the player who completes the room is rewarded with. Make sure you don't allow the caller to modify the actual set!
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public Map<Weapon,Integer> getAmmoWonUponCompletion(){
        return Collections.unmodifiableMap(this.ammoWonUponCompletion);
    }

    /**
     *
     * @return The amount of health this room
     */
    public int getHealthWonUponCompletion(){
        return this.healthWonUponCompletion;
    }

    /**
     * @return indicates if all the monsters in the room are dead
     */
    public boolean isCompleted(){
        return this.liveMonsters.isEmpty();
    }

    /**
     * @return The SortedSet of all monsters in the room
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public SortedSet<Monster> getMonsters(){
        return Collections.unmodifiableSortedSet(this.allMonsters);
    }

    /**
     * @return the set of monsters in this room that are alive
     */
    public SortedSet<Monster> getLiveMonsters(){
        return Collections.unmodifiableSortedSet(this.liveMonsters);
    }
    protected SortedSet<Monster> getLiveMonstersActualSet(){
        return this.liveMonsters;
    }
    protected Monster getMonster(MonsterType type){
        for(Monster a:this.getLiveMonsters()){
            if(a.getMonsterType().equals(type)){
                return a;
            }
        }
        return null;
    }

    /**
     * Every time a player enters a room, he loses health points based on the monster in the room.
     * The amount lost is the sum of the values of playerHealthLostPerExposure of all the monsters in the room
     * @return the amount of health lost
     * @see MonsterType#playerHealthLostPerExposure
     */
    public int getPlayerHealthLostPerEncounter(){
        int healthLostPerExposure = 0;
        for(Monster a:this.liveMonsters){
            healthLostPerExposure += a.getMonsterType().playerHealthLostPerExposure;
        }
        return healthLostPerExposure;
    }

    /**
     * @return the set of monsters in this room that are dead
     */
    public SortedSet<Monster> getDeadMonsters(){
        return Collections.unmodifiableSortedSet(this.deadMonsters);
    }
}
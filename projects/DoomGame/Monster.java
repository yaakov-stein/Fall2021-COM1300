package edu.yu.cs.intro.doomGame;

/**
 * A specific monster
 */
public class Monster implements Comparable<Monster>{
    private MonsterType protector;
    private MonsterType monsterType;
    private Room currentRoom;
    private int remainingLife;
    private boolean dead = false;
    /**
     * create a monster with no custom protectors; its protectors will be determined by its MonsterType
     * @param type the type of monster to create
     */
    protected Monster(MonsterType type){
        this(type,type.getProtectedBy());
    }
    /**
     * create a monster with a custom protector, i.e. a different protector than the one specified in its MonsterType
     * @param type
     * @param customProtectedBy
     */
    public Monster(MonsterType type, MonsterType customProtectedBy){
        this.monsterType = type;
        this.protector = customProtectedBy;
        this.remainingLife = this.getMonsterType().ammunitionCountNeededToKill;
    }

    /**
     * set the room that the Monster is located in
     * @param room
     */
    protected void setRoom(Room room){
        this.currentRoom = room;
    }
    public MonsterType getMonsterType(){
        return this.monsterType;
    }

    /**
     * Attack this monster with the given weapon, firing the given number of rounds at it
     * @param weapon
     * @param rounds
     * @return indicates if the monster is dead after this attack
     * @throws IllegalArgumentException if the weapon is one that doesn't hurt this monster, if the weapon is null, or if rounds < 1
     * @throws IllegalStateException if the monster is already dead
     */
    protected boolean attack(Weapon weapon, int rounds){
        if(weapon == null || rounds < 1 || weapon.ordinal() < this.getMonsterType().weaponNeededToKill.ordinal()){
            throw new IllegalArgumentException("Invalid Weapon/number of Rounds");
        }
        if(this.isDead()){
            throw new IllegalStateException("Monster is already Dead");
        }
        this.remainingLife = this.getMonsterType().ammunitionCountNeededToKill - rounds;
        if(this.remainingLife <= 0){
            this.dead = true;
            return true;
        }
        return false;
    }
    protected void setDead(){
        this.dead = true;
    }
    /**
     * @return is this monster dead?
     */
    public boolean isDead(){
        return this.dead;
    }

    /**
     * if this monster has its customProtectedBy set, return it. Otherwise, return the protectedBy of this monster's type
     * @return ?????????
     */
    public MonsterType getProtectedBy(){
        return this.protector;
    }

    /**
     * Used to sort a set of monsters into the order in which they must be killed, assuming they are in the same room.
     * If the parameter refers to this monster, return 0
     * If this monster is protected by the other monster's type, return 1
     * If this monster's type protects the other monster, return -1
     * If this monster's ordinal is < the other's, return -1
     * If this monster's ordinal is > the other's, return 1
     * If(this.hashCode() < other.hashCode()), then return -1
     * Otherwise, return 1
     * @param other the other monster
     * @return see above
     */
    @Override
    public int compareTo(Monster other) {
        if(other == this){
            return 0;
        }else if(this.protector == other.getMonsterType()){
            return 1;
        }else if(other.getProtectedBy() == this.getMonsterType()){
            return -1;
        }else if(this.getMonsterType().ordinal() < other.getMonsterType().ordinal()){
            return -1;
        }else if(this.getMonsterType().ordinal() > other.getMonsterType().ordinal()){
            return 1;
        }else if(this.hashCode() < other.hashCode()){
            return -1;
        }
        return 1;
    }
}
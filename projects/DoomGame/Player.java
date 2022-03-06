package edu.yu.cs.intro.doomGame;
import java.util.*;

public class Player implements Comparable<Player> {
    private Map<Weapon,Integer> weaponStock = new HashMap<>();
    private SortedSet<Weapon> weaponSet = new TreeSet<>();
    private int currentHealth;
    private final String name;
    public Player(String name, int health) {
        this.name = name;
        this.currentHealth = health;
        this.weaponSet.add(Weapon.FIST);
    }
    public String getName(){
        return this.name;
    }
    public boolean hasWeapon(Weapon w){
        return this.weaponStock.containsKey(w);
    }
    public int getAmmunitionRoundsForWeapon(Weapon w){
        if(w == Weapon.FIST){
            return Integer.MAX_VALUE;
        }
        if(hasWeapon(w)){
            return this.weaponStock.get(w);
        }
        return 0;
    }
    /**
     * Change the ammunition amount by a positive or negative amount
     * @param weapon weapon whose ammunition count is to be changed
     * @param change amount to change ammunition count for that weapon by
     * @return the new total amount of ammunition the player has for the weapon.
     */
    public int changeAmmunitionRoundsForWeapon(Weapon weapon, int change){
        if(weapon == Weapon.FIST){
            return Integer.MAX_VALUE;
        }
        if(this.weaponStock.containsKey(weapon)){
            this.weaponStock.replace(weapon,Math.max(this.weaponStock.get(weapon) + change,0));
        }else{
            this.weaponStock.put(weapon,Math.max(change,0));
        }
        return this.weaponStock.get(weapon);
    }

    /**
     * A player can have ammunition for a weapon even without having the weapon itself.
     * @param weapon weapon for which we are adding ammunition
     * @param rounds number of rounds of ammunition to add
     * @return the new total amount of ammunition the player has for the weapon
     * @throws IllegalArgumentException if rounds < 0 or weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected int addAmmunition(Weapon weapon, int rounds){
        if(rounds < 0 || weapon == null){
            throw new IllegalArgumentException("Weapon is null or entered negative value of ammunition");
        }
        if(this.isDead()){
            throw new IllegalStateException("Player is dead");
        }
        if(weapon == Weapon.FIST){
            return Integer.MAX_VALUE;
        }
        this.changeAmmunitionRoundsForWeapon(weapon,rounds);
        return this.weaponStock.get(weapon);
    }
    /**
     * When a weapon is first added to a player, the player should automatically be given 5 rounds of ammunition.
     * If the player already has the weapon before this method is called, this method has no effect at all.
     * @param weapon
     * @return true if the weapon was added, false if the player already had it
     * @throws IllegalArgumentException if weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected boolean addWeapon(Weapon weapon){
        if(weapon == null){
            throw new IllegalArgumentException("Weapon is null");
        }
        if(this.isDead()){
            throw new IllegalStateException("Player is dead");
        }
        boolean weaponNewlyAdded = this.weaponSet.add(weapon);
        if(weaponNewlyAdded){
            if(this.weaponStock.containsKey(weapon)) {
                this.weaponStock.replace(weapon,this.weaponStock.get(weapon) + 5);
            }else{
                this.weaponStock.put(weapon,5);
            }
        }
        return weaponNewlyAdded;
    }
    public int changeHealth(int amount){
        if(this.isDead()){
            throw new IllegalStateException("Player is dead");
        }
        return this.currentHealth += amount;
    }
    protected void setHealth(int amount){
        this.currentHealth = amount;
    }
    public int getHealth(){
        return this.currentHealth;
    }
    public boolean isDead(){
        return currentHealth <= 0;
    }
    protected Set<Weapon> getWeaponSet(){
        return this.weaponSet;
    }

    /**
     * Compare criteria, in order:
     * Does one have a greater weapon?
     * If they have the same greatest weapon, who has more ammunition for it?
     * If they are the same on weapon and ammunition, who has more health?
     * If they are the same on greatest weapon, ammunition for it, and health, they are equal.
     * Recall that all enums have a built-in implementation of Comparable, and they compare based on ordinal()
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Player other) {
        if(this == other){
            return 0;
        }
        Weapon greatestWeaponA = this.weaponSet.last();
        int highestOrdinalA = greatestWeaponA.ordinal();
        Weapon greatestWeaponB = ((SortedSet<Weapon>)other.getWeaponSet()).last();
        int highestOrdinalB = greatestWeaponB.ordinal();
        if(highestOrdinalA - highestOrdinalB != 0){
            return Integer.compare(highestOrdinalA,highestOrdinalB);
        }else{
            int ammunitionA = this.getAmmunitionRoundsForWeapon(greatestWeaponA);
            int ammunitionB = other.getAmmunitionRoundsForWeapon(greatestWeaponB);
            if(ammunitionA - ammunitionB != 0){
                return ammunitionA - ammunitionB;
            }else{
                return this.getHealth() - other.getHealth();
            }
        }
    }
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
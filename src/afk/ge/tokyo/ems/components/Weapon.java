package afk.ge.tokyo.ems.components;

/**
 *
 * @author daniel
 */
public class Weapon {
    
    public float range, damage, speed;
    public float fireInterval, timeSinceLastFire, healthPenalty;
    public int ammo;

    public Weapon(float range, float damage, float speed, float fireInterval, int ammo, float healthPenalty)
    {
        this.range = range;
        this.damage = damage;
        this.speed = speed;
        this.fireInterval = fireInterval;
        this.timeSinceLastFire = fireInterval;
        this.ammo = ammo;
        this.healthPenalty = healthPenalty;
    }
    
    
}

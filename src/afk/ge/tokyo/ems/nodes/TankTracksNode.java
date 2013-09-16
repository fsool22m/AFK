package afk.ge.tokyo.ems.nodes;

import afk.ge.tokyo.ems.Node;
import afk.ge.tokyo.ems.components.Controller;
import afk.ge.tokyo.ems.components.Motor;
import afk.ge.tokyo.ems.components.State;
import afk.ge.tokyo.ems.components.TankController;
import afk.ge.tokyo.ems.components.Velocity;
import afk.ge.tokyo.ems.components.Weapon;

/**
 *
 * @author daniel
 */
public class TankTracksNode extends Node
{
    public Controller controller;
    public TankController tankController;
    public State state;
    public Velocity velocity;
    public Weapon weapon;
    public Motor motor;
}

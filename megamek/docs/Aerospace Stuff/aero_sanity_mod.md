# Aero Sanity Mod Information

11/7/2013 - Jay Lawson (Taharqa)

## WARNING - August 2016

**This option has been known to cause issues in both MegaMek and MekHQ. The developer on this Mod is no longer active
and future support will depend on his return.**

## Description

Individuals not used to including aerospace units in their games are often surprised at some of the odd ways the rules
for damage work.

* SRMs do damage in 5-point clusters.
* LRM20's mounted in weapon bays on DropShips can actually head cap meks.
* UAC20s do one hit for 20 points of damage and another for 10 points of damage.
* Medium laser lasers and PPCs do the same amount of capital scale damage.
* An entire fighter squadron can train its weapons on a single fighter in the other
  squadron.
* and so forth.

Most of these issues stem from the difficulty in trying to merge rules from the old BattleSpace rules with capital class
vessels and fighter squadrons with the ground game in BattleTech. To keep things simple, attack value was implemented
and a host of issues have followed. This is understandable for the tabletop game where you don't want to roll the dice
on the cluster table for the 6 LRM20s that are in the weapon bay and then apply the results in 5-point clusters.
However, for MegaMek that calculation is almost instantaneous. The Aero Sanity mod is my attempt to take advantage of
the computing power of MegaMek to create damage rules that are consistent across unit types and allow the differences in
weapons at the ground level to carry to the aerospace level.

## What Does The Aero Sanity Mod Do?

The aero sanity mod effects how aero units dish out, receive, and take damage. Specifically, here are the rules:

1) All armor values are represented at the standard level. This means that a warship with 50 points of armor
   under regular rules will show up under this mod as having 500 points. This allows for different non-capital
   scale weapons to different amounts of damage rather than just being rounded up. So a medium laser will take
   5 points off of a capital scale warship's armor and a PPC will take off 10. Also, to be perfectly clear,
   capital scale weaponry gets its damage multiplied by 10 for all cases to keep the scales consistent (e.g., a
   NAC20 does 200 points of damage).
    1) SI points are multiplied by 2, and we don't do the stupid "divide damage by 2 against the SI" because that is
       perhaps the most worthless rule ever invented. This means that capital scale SI gets multiplied by 20, given
       the former rule.
2) For non-squadron and non-bay weapon units, all weapons follow their standard rules for cluster firing. So, SRMs
   will divvy out in 2 point increments, LBXs in 1, and LRMs in 5. This is actually also true of squadrons and
   bay-weapon units but requires some additional explanation.
3) Bay weapons operate like "linked weapons" from the stratOps rules. One hit roll is made, but each weapon rolls
   a different location. Cluster weapons also roll on the cluster hit table and then roll in the proper damage
   increments to different locations. So, if a weapon bay with 3 PPCs hit a target it would roll three different
   hit locations and apply 10 points of damage each time. If a weapon bay with 2 LRM20s hit, you would roll on the
   cluster hit table twice and then apply damage in 5-point increments for each LRM. So no more head-capping LRMs.
4) Weapon squadrons follow rules similar to weapon bays. After rolling to see how many weapons hit after a successful
   hit, you then roll separate hit locations for each weapon. So a squadron of fighters with 12 medium lasers who
   hit with 8 of them would roll 8 different hit locations on the unit they hit. For cluster weapons, you roll on the
   cluster hit table for each weapon that hit and sum up the total number of missiles/shells/whatever and then divvy
   that out in whatever increments are appropriate for the weapon (5 points, 2 points, etc.)
5) The only caveat to cluster weapons working normally is that when a cluster weapon hits a capital scale unit, it is
   assumed that all of its shots hit, although you still divvy them up in the normal sized increments. So a fighter
   with an LRM20 that hits a warship would do four 5-point clusters of damage automatically.

That's it. When these rules are combined, I think they work seamlessly to make the damage rules work across all
different
unit types. The one downside to the rules is that you can get longer firing reports do to all the damage being divided
up for cluster weapons. They are also still in beta form, so if you find bugs or have suggestions on how they could
be improved, please let me know at <bombaijin@users.sourceforge.net>

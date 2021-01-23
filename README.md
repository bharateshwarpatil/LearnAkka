# LearnAkka


### What is the Difference Between Actor Reference and Path?
An actor reference designates a single actor and the life-cycle of the reference matches that actor’s life-cycle; an actor path represents a name which may or may not be inhabited by an actor and the path itself does not have a life-cycle, it never becomes invalid. You can create an actor path without creating an actor, but you cannot create an actor reference without creating a corresponding actor.


### How are Actor References obtained?
There are two general categories to how actor references may be obtained: by creating actors or by looking them up through the Receptionist.

### Top-Level Scopes for Actor Paths
At the root of the path hierarchy resides the root guardian above which all other actors are found; its name is "/". The next level consists of the following:
* "/user" is the guardian actor for all user-created top-level actors; actors created using ActorSystem.actorOf are found below this one.
* "/system" is the guardian actor for all system-created top-level actors, e.g. logging listeners or actors automatically deployed by configuration at the start of the actor system.
* "/deadLetters" is the dead letter actor, which is where all messages sent to stopped or non-existing actors are re-routed (on a best-effort basis: messages may be lost even within the local JVM).
* "/temp" is the guardian for all short-lived system-created actors, e.g. those which are used in the implementation of ActorRef.ask.
* "/remote" is an artificial path below which all actors reside whose supervisors are remote actor references
The need to structure the name space for actors like this arises from a central and very simple design goal: everything in the hierarchy is an actor, and all actors function in the same way.

## Actor lifecycle

It is important to note that actors do not stop automatically when no longer referenced, every Actor that is created must also explicitly be destroyed. The only simplification is that stopping a parent Actor will also recursively stop all the child Actors that this parent has created. All actors are also stopped automatically when the ActorSystem is shut down.

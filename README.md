# BrickBreakerGame

## Compilation Instructions

### Prerequisites
Ensure you have the following dependencies installed before proceeding:

- Oracle OpenJDK 21.0.1
- Latest JavaFX
- SceneBuilder

### Compilation Steps

1. **Load Project into IDE:**
   - Clone the repository to your local machine.
   - Open the project in your preferred Integrated Development Environment (IDE).

2. **Configure JavaFX and SceneBuilder:**
   - Set up JavaFX and SceneBuilder in your IDE according to its documentation.

3. **Run the Application:**
   - Execute `Main.java` to launch the application.

## Implemented and Working Features

- **Game Storyline:**
  - Three stages: easy, hard, and boss levels.
  - Boss with a substantial life bar and launching missiles.
  - Harder levels with different brick arrangements.
  
- **Menu System:**
  - Start game and load game buttons.
  - Smooth transition between menu and game scenes.

- **Enhancements:**
  - Revamped collision detection system.
  - Button animations on hover.
  - Bricks initialized using an array of images.
  - Configuration properties in `config.properties` retrieved by `Config.java`.
  - Ball initialized on top of the paddle and launched with the UP key.
  - Player recenters and regains the ball after losing a life.
  - Indestructible block.

## Implemented but Not Working Properly

- **Load Game Method:**
  - Serialization issues preventing proper loading.
    
- **RestartGame Method:**
  - Not working due to other utilized methods and variables being static, therefore the PrimaryStage calls a NULL

## Features Not Implemented

- **Explosion Effect:**
  - Time constraints led to its exclusion, but the `Explode.java` functionality is present.

- **??? Block:**
  - Time constraints prevented the implementation, but assets (`mystery.png`, `mystery0.png`, etc.) are included.

## New Java Classes

1. **Bullet.java:**
   - Purpose: to handle all things related to the boss firing bullets

2. **Explode.java:**
   - Purpose: create an explosion effect on the Scene from a sprite sheet

3. **Config.java:**
   - Purpose: to get configurations from config.properties file

4. **GameView.java:**
   - Purpose: to add certain objects to the game view

## Modified Java Classes

- **Overall Modifications:**
  - Removed unnecessary methods and variables.
  - Fixed game logic.
  - Utilized `java.logging` for exception handling.
  - Renamed variables and fixed typos.
  - Used static final variables.
  - Adjusted variable types.
  - Simplified game logic.
  - Implemented lambda expressions.
  - Organized methods into appropriate classes.
  - Employed type casting to avoid errors.

## Unexpected Problems

- **JavaFX Application Thread:**
  - Handling the JavaFX Application Thread to avoid artifacts and visual bugs.

- **OOP Challenges:**
  - Grasping and applying Java Object-Oriented Programming concepts was challenging.

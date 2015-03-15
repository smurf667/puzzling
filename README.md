# puzzling
A touch screen based n-puzzle game

"Puzzling" is a n-puzzle game with n=8, n=15 or n=24 (for hard core players). The game can be played on mobile devices supporting Java with a touch screen and a HVGA (320x480 pixels) resolution.

Here are the screens of the game:

![Screenshots](https://raw.githubusercontent.com/smurf667/puzzling/master/Puzzling/templates/thumb.png)

# Playing
## Main menu
The main menu shows at most sixteen images that can be played. To play, select one of the images and then tap the yellow play button (a right-facing triangle). You can also set the difficulty with the button right next to the play button.

You can add, edit and delete images from the menu. You can do this by tapping the plus or minus button.

To exit the game you can press the red button marked with an X.

## The puzzle
Once you have started a game, the tiles are randomly shuffled. Your task is to restore the image. You can do this by sliding the tiles until the image is complete. A timer is shown while you play, counting up. When you have solved the puzzle, the timer stop - if you beat the highscore, the time will be shown in a red frame. Highscores are tracked per difficulty level and not per image.

There are four buttons at the bottom of the screen; the green button allows you to solve the puzzle immediately. The yellow button next to it will reshuffle the tiles. The blue button next to the yellow one toggles the sound effects on and off.
Finally, the red button allows you to quit the game and return to the main menu.

## Image sources
When you click the plus button on the main menu you can enter or edit an image source. Typically you would enter a HTTP URL here, but it should also be possible to enter local files.
When entering characters you can toggle between lower case and upper case by tapping the black space above the virtual keyboard.

Hint: If you want to get rid of the "airtime prompt" you can delete the two images in the second row of the main screen (out of the box content). Beware that changes are usually persisted.

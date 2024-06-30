# Task Tracker

Task-Tracker is an Android App to track your tasks which you have to do.

## Build and Run:

To build and run the Application in your mobile you have to follow some steps.

- Clone the GitHub repository to you machine by using `git`
```bash
    git clone https://github.com/asfakur-rahat/Task-Tracker.git
```
- Now open the project to your android studio from `File/open`\
  <img src="images/openproject.png" alt="Tasklist" width="25%"/>
- Android studio should recognize the project as android project and start build Gradle process wait for some time and let the process finish.
- Download the `google-services.json` file from [here](https://drive.google.com/file/d/1vKn2qjSJmnrjaBsmcGwB-hYWpsCpa7NM/view?usp=sharing "google-service.json") and put it to app folder\
  <img src="images/service.png" alt="google-services.json" width = "40%"/>
- After that you can click on run to run the app to your Emulator or a Physical device.

## About Task-Tracker:
By using task tracker you can now keep track of your task.\
Just create a task and you are ready to keep track of it.\
You can edit a created task or delete the task anytime you want\
and also you can mark the task as done if you want.
### Features:
- Task List Page
- Add New Tasks
- Delete an existing task
- Edit an existing task
- Add an image from your device to make the task more describable

### Screen shots of the features:
- **Task List Screen** \
  <img src="images/tasklist-light.jpg" alt="Tasklist" width="25%"/>
  <img src="images/tasklist-dark.jpg" alt="Tasklist" width="25%"/>
- **Add New Task Screen** \
  <img src="images/addtask-light.jpg" alt="Add Task" width="25%"/>
  <img src="images/addtask-dark.jpg" alt="Add Task" width="25%"/>
- **Task Details Screen & delete option** \
  <img src="images/taskdetails-light.jpg" alt="Task details" width="25%"/>
  <img src="images/taskdetails-dark.jpg" alt="Task details" width="25%"/>
- **Edit Task Screen** \
  <img src="images/edittask-light.jpg" alt="Edit Task" width="25%"/>
  <img src="images/edittask-dark.jpg" alt="Edit Task" width="25%"/>
- **Bottom Sheet to give user options** \
  <img src="images/options-light.jpg" alt="Bottom Sheet Dialog" width="25%"/>
  <img src="images/options-dark.jpg" alt="Bottom Sheet Dialog" width="25%"/>

### Sample Video demontration of app functionality:

  <img src="images/appdemo.gif" alt="Bottom Sheet Dialog" width="25%"/>

## Assumptions & decisions made during development.

- Using Coil for load image in image view
- Only call JSONPlaceholder API if the room databse is empty
- Disabled allpast date & time so that user can't pick the past time
- handled null image case manually so it doesn't make any error in firebase storage
- for MVVM Architecture i tried to follow this image.\
   <img src="images/mvvm.jpg" alt="Bottom Sheet Dialog" width="50%"/>
- And tried to commit to git whenever a stable feature was complete.
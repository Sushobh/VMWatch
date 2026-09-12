# VMWatch (Experimental)

Android library and desktop app to view viewmodel (associated with activities,fragment and navgraphs) properties as you are using the app. 


## Goal behind this project

Finding the viewmodel state is a crucial part of debugging Android apps. However debugging in Android Studio can be slow and disrupt the normal app execution.
What if you could view the state of the viewmodel without pausing the app? What if you could just click a button and view the state of the viewmodel?

## Demo

https://github.com/user-attachments/assets/d5837e05-9889-44b4-896d-20ea76b0c790

### How to use

First add this line in the app's gradle file

```java
   dependencies {
		   implementation("com.sushobh:fraglens:0.1.15")
    }
```

then download the  desktop app (its a zip file with a dmg (mac) or msi installer (windows) inside of it)

[Download the Mac Desktop app](https://github.com/Sushobh/VMWatch/actions/runs/18656737201/artifacts/4318569184)

[Download the Windows Desktop app](https://github.com/Sushobh/VMWatch/actions/runs/18657476109/artifacts/4318874875)

then run this adb command. To forward the mobile app server to the desktop.

```
   adb forward tcp:56441 tcp:56440
```


### How it works

VMWatch has two components.

1. Android library - Picks up the viewmodels using reflection based on the viewmodelstore owner. Then uses reflection again to parse the properties and create displayable data.
   Next, it starts a http server on the app exposing apis that return the list of current viewmodels and their respective states.
2. Desktop app - I use compose desktop app to communicate with the api exposed by the mobile app.

### Limitations

1. Tries to serialize field values using gson, if gson fails, tries it with Moshi. If both fail, returns the toString() value.
2. Runs via adb port forwarding, I had planned to automate that process too using ddmlib but decided against it because a computer can have multiple adb devices connected and it would have been cumbersome observing the 
   device list and running port forwards based on the device id etc.







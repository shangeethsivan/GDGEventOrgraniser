# GDGEventOrganiser

This App was created in a Day for smoothening the registration process at GDG Devfest Chennai 2022 (IN-PERSON EVENT). https://devfest.gdgchennai.in/

I know creating an App in a Day is something interesting. 

### Main Features of the app

* Orgranisers can Check IN attendees with check in code from townscript/other source
* QR Code scanner for scanning the code that townscript has provided
* User Check OUT screen for the user mainly used for providing goodies for the attendees (To avoid giving two goodies for a single attendee)
* Live Check IN count for organisers
* Features are subject to change will be creating a slack channel for this project and will share the link here interested ppl can join and contribute.
* Has a screen to upload CSV data to firebase firestore (Its kind of tricky now where you have to add the data manually to the project the open the screen to upload need to fix this!)

### Tech Stack
* Architecture wise for now dont expect anything since we rushed in creating it. [MVVM/MVI preferred]
* Compose for UI 
* Firebase Firestore for DB
* Firebase Crashlytics for crash monitoring
* Firebase Authentication for authenticating organisers
* Planning to properly move this to a MVVM architecture

### Features Planned / Under Dev: 
* Download Checked in users data as CSV File.
* Create a generic app that can be used by all GDG communities world wide for registration and check-ins.

### Stuff we would like to have (Mainly Infra)
🚧 Use Hilt for DI
🚧 Move all pages to follow MVVM architecture
* CI (Need to choose on available options)
* Use NavHost in compose and NavRail

Completed Tasks
✅ Create TOML for dependancy management
✅ Add renovate plugin to manage and update dependancies

External libraries used :
https://github.com/G00fY2/quickie


### App Screenshots
<img src="https://user-images.githubusercontent.com/9254310/210180939-e1dfab1b-604c-4338-b17c-12d277aaf389.jpg" alt="" height="400" />    <img src="https://user-images.githubusercontent.com/9254310/210180941-48489b8e-2723-487f-9828-a4b9653997b2.jpg" alt="" height="400" />

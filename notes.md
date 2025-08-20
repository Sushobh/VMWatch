## Publishing to Maven central using gpg.

0. Make sure the gradle folder for the project is the right one and add the credentials mentioned in
   the gradle.properties
   of that location.

1. Generate a GPG key,export it to secring file and publish to a key server.
   Noting down the the secring path is important because. I am not sure if the keys
   are automatically exported to secring file or not. So I export the keys manually just to be sure.
   The secring file path is important so take note of it by listing the keys.
   Note that the secring file is just a file which acts like a store of keys.
```
gpg --gen-key
gpg --list-keys
gpg --keyserver keyserver.ubuntu.com --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
gpg --export-secret-keys B02B02FAC53969CFCAF76EA67F0C8FD537C63BA2 > C:/Users/Sushobh/AppData/Roaming/gnupg/secring.gpg
```
2. Then use it in global gradle.properties file
The properties file should look this
```
mavenCentralUsername=//The username obtained via generating the token on MAVEN CENTRAL
mavenCentralPassword=//The username obtained via generating the token on MAVEN CENTRAL
signing.keyId=//The last 8 digits of the key id.
signing.password=//The passpharase used while generating the gpg key.
signing.secretKeyRingFile=//The location of the secring file
```


3. Publishing
./gradlew moduleName:publishAndReleaseToMavenCentral --no-configuration-cache
# tgtp - a Terminal based client for ChatGTP

An interactive Spring-Boot based shell application to interact with ChatGTP. It currently supports the following
features:

- Chat with the AI
- Create a Spotify Playlist based on your definition
- Show the conversions history

## Prerequisites

### System Access

- ChatGTP API Access
- Spotify Client-ID and Client-Secret as well as a refresh token
    - To get a refresh token you can
      use [rmjstn/spotify-refresh-token: Get a Spotify refresh token](https://github.com/rmjstn/spotify-refresh-token/tree/main)
    - if you don't want to use this feature, just leave the respective entries in the config file empty
- Since the build creates a native image, you need `GraalVM` (Java version 17 or higher)

### Config File

The application expects the following config file `${user.home}/.config/tgtp.conf` to exist. It needs to have the
following format:

```properties
tgtp.openai.api.token=<api token>
tgtp.spotify.clientId=<client ID>
tgtp.spotify.clientSecret=<client secret>
tgtp.spotify.refreshToken=<refresh token>
tgtp.spotify.userId=<user ID>
```

## Build

```shell
mvn clean package -Pnative
```

## Run

Currently opening the binary is not yet working. You need to open it with java:

```shell
java -jar ./bootstrap/target/tgtp-exec.jar
```

### Notes

- use `help` to get an overview of the available commands
- use `help <command>` to get details of the command
- when your message provided for a command has multiple words you have to wrap quotes around the text. Spring Shell
  supports multi line queries. Allowed quotes are:  `'`, `'''`, `"` or `"""`

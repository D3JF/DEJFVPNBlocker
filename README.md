# DEJFVPNBlocker
 A simple VPN blocker for Minecraft Beta 1.7.3 / CB 1060.

## How to use

###### 100% free to use! There is no expensive VPN checking service you have to pay for and no complicated configuration.

The plugin automatically kicks any player with an IP suspected to belong to a VPN service. You can customize the kick message by editing the automatically generated config file. If a player is being kicked erroneously, you may also whitelist their IP address in the config file or through a command.

**whatismyaddress.com** is queried to check for VPNs, which is technically against ToS of the website, but the query rate is low and essentially drops to zero once a cache of IPs builds up, so there should be no problems. Every checked IP gets stored in a database for a default of 7 days. This period can be modified in the plugin's configuration, but we recommend keeping it low in case a new VPN service's IP gets re-evaluated and properly detected as belonging to a VPN.


## Commands

These are the currently usable plugin commands:

### Main command

The main plugin command is `/vpnblocker`, which shows help (also accessible with `/vpnblocker help`).

### Check an IP address

If you wish to check an arbitrary IP address to see if it belongs to a VPN or not, you may use `/vpnblocker check ADDRESS`.

### Check a player

If you wish to find out a player's IP address and whether they are using a VPN or not, you may use `/vpnblocker check PLAYERNAME`.

### Whitelist an IP address

If you wish to whitelist an IP address that is being detected as a VPN and do not wish to edit configuration files, you may use `/vpnblocker whitelist ADDRESS` to add this address to the whitelist. To remove an IP from the whitelist, use `/vpnblocker unwhitelist ADDRESS`.

### Blacklist an IP address

Similarly to whitelisting, you may also blacklist an IP address using `/vpnblocker blacklist ADDRESS` to add an address to the whitelist. Any player trying to join with this IP will be instantly kicked. This acts like a rudimentary IP ban, which may come in handy if standard IP bans don't work for you. To remove an IP from the whitelist, use `/vpnblocker unblacklist ADDRESS`.


## Configuration

This is approximately what your default configuration file (`config.cfg`) should look like:

```yml
databaseClearIntervalSeconds: 604800
whitelistedIps:
- 0.0.0.0
- 127.0.0.1
blacklistedIps:
- Your blacklisted IP here
vpnKickMessage: VPN connections aren't allowed!
blacklistKickMessage: Your IP has been blacklisted!
```

Explained:

`databaseClearIntervalSeconds` - In seconds, this is how long an IP log will stay in the local database before it gets cleared (default 7 days).

`whitelistedIps` - IP addresses in this list will always be allowed into the server, even if they get detected as a VPN service.

`blacklistedIps` - IP addresses in this list will always be kicked from the server. Basically an IP ban.

`vpnKickMessage` - The message that is shown to a player if they get kicked for using a VPN.

`blacklistKickMessage` - The message that is shown to a a player if they get kicked for having a blacklisted IP address.

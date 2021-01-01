# DEJFVPNBlocker
 A simple VPN blocker for Minecraft Beta 1.7.3 / CB 1060.

## How to use

The plugin automatically kicks any player with an IP suspected to belong to a VPN service. You can customize the kick message by editing the automatically generated config file. If a player is being kicked erroneously, you may also whitelist their IP address in the config file or through a command.

**whatismyaddress.com** is queried to check for VPNs, which is technically against ToS of the website, but the query rate is low and essentially drops to zero once a cache of IPs builds up, so there should be no problems. Every checked IP gets stored in a database for a default of 7 days (as of version 1.1). This period can be modified in the plugin's configuration, but we recommend keeping it low in case a new VPN service's IP gets re-evaluated and properly detected as belonging to a VPN.

## Commands

These are the currently usable plugin commands:

### Main command

The main plugin command is `/vpnblocker`, which shows help (also accessible with `/vpnblocker help`).

### Check an IP address

If you wish to check an arbitrary IP address to see if it belongs to a VPN or not, you may use `/vpnblocker help ADDRESS`, replacing `ADDRESS` with the IP you are checking.

### Whitelist an IP address

If you wish to whitelist an IP address that is being detected as a VPN and do not wish to edit configuration files, you may use `/vpnblocker whitelist ADDRESS` to add this address to the whitelist. To remove an IP from the whitelist, use `/vpnblocker unwhitelist ADDRESS`.

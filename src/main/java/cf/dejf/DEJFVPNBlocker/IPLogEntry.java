package cf.dejf.DEJFVPNBlocker;

import java.util.Date;

public class IPLogEntry {

    private boolean vpn;
    private Date checkDate;

    public IPLogEntry(boolean vpn, Date checkDate) {
        this.vpn = vpn;
        this.checkDate = checkDate;
    }

    public Boolean isVPN() { return vpn; }
    public Date getCheckDate() { return checkDate; }

}

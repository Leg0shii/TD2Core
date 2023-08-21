package de.legoshi.td2core.map.session;

import lombok.*;
import org.bukkit.Location;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParkourSession {
    
    private int totalFails;
    private long totalPlayTime;
    
    private Location lastMapLocation;
    
    private Location lastPracLocation;
    private Location pracCPLocation;
    
    private Location lastCheckpointLocation;
    private Location nextCP;
    
    private Date started;
    private Date sessionStarted;
    private Date finished = null;
    
    private boolean passed;
    private int currentCPCount;
    private long playTime;
    private int fails;
    
    public boolean justFinished() {
        return finished != null;
    }
    
    public long getPlayTime() {
        return playTime + System.currentTimeMillis() - sessionStarted.getTime();
    }
    
    public long getPausedTime() {
        return playTime;
    }
    
    public void reset() {
        totalFails = 0;
        totalPlayTime = 0;
        lastMapLocation = null;
        lastCheckpointLocation = null;
        lastPracLocation = null;
        pracCPLocation = null;
        started = null;
        sessionStarted = new Date(System.currentTimeMillis());
        finished = null;
        playTime = 0;
        fails = 0;
    }
    
    public ParkourSession copy() {
        ParkourSession session = new ParkourSession();
        session.totalFails = totalFails;
        session.totalPlayTime = totalPlayTime;
        session.lastMapLocation = lastMapLocation;
        session.lastCheckpointLocation = lastCheckpointLocation;
        session.lastPracLocation = lastPracLocation;
        session.pracCPLocation = pracCPLocation;
        session.started = started;
        session.sessionStarted = sessionStarted;
        session.finished = finished;
        session.playTime = playTime;
        session.fails = fails;
        return session;
    }
    
}
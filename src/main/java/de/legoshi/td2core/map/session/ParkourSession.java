package de.legoshi.td2core.map.session;

import lombok.*;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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

    private boolean isNoSprint = false;
    private List<PotionEffect> currentEffects = new ArrayList<>();
    private int timeTillNextTicks = - 1;
    private long currTimeTillNext;

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

    public int getTimeTillNextTicks() {
        return timeTillNextTicks;
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
        timeTillNextTicks = -1;
        isNoSprint = false;
        currentEffects = new ArrayList<>();
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
        session.timeTillNextTicks = timeTillNextTicks;
        session.currTimeTillNext = currTimeTillNext;
        session.isNoSprint = isNoSprint;
        session.currentEffects = new ArrayList<>(currentEffects);
        return session;
    }
    
}

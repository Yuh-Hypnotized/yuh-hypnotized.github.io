package com.fishyfire.fishyfiremod.events;

/**
 * Simple cross-side helper to request that the client opens a GUI on the next client tick.
 * This class is safe to reference from common code (no client-only imports).
 */
public class GuiOpener {

    private static volatile boolean openRequested = false;
    private static volatile boolean openPlayerTrackerRequested = false;

    public static void requestOpen() {
        openRequested = true;
    }

    public static void requestOpenPlayerTracker() {
        openPlayerTrackerRequested = true;
    }

    /**
     * Atomically check and consume the request.
     * @return true if there was a request and it is consumed
     */
    public static boolean consumeRequest() {
        if (openRequested) {
            openRequested = false;
            return true;
        }
        return false;
    }

    public static boolean consumePlayerTrackerRequest() {
        if (openPlayerTrackerRequested) {
            openPlayerTrackerRequested = false;
            return true;
        }
        return false;
    }
}


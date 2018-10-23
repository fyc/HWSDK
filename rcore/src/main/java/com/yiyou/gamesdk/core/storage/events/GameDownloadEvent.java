package com.yiyou.gamesdk.core.storage.events;



public class GameDownloadEvent {

    public static final String TYPE_GAME_DOWNLOAD = "com.yiyou.gamesdk.events.type.gamedownload";

    public static class GameDownloadEventParam {
    	
        public int currentSize ;
        public int totalSize ;
        
        public GameDownloadEventParam() {
        }

        public GameDownloadEventParam(int current ,int total) {
        	this.currentSize = current ;
        	this.totalSize = total ;
        }

    }
}

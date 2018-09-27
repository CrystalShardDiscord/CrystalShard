package de.kaleidox.util;

import java.io.File;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class FileType {
    private final String regexPiece;
    
    FileType(String regexPiece) {
        this.regexPiece = regexPiece;
    }
    
    public boolean isType(String test) {
        return test.matches(".+" + regexPiece);
    }
    
    public boolean isType(File file) {
        return isType(file.getName());
    }
    
    @Override
    public String toString() {
        return regexPiece;
    }
    
    public static class IMAGE extends FileType {
        // Static Fields
        public final static IMAGE   PNG    = new IMAGE(".png");
        public final static IMAGE   JPEG   = new IMAGE(".jpe?g");
        public final static IMAGE   WebP   = new IMAGE(".webp");
        public final static IMAGE   GIF    = new IMAGE(".gifv?");
        public final static IMAGE[] ALL    = new IMAGE[]{PNG, JPEG, WebP, GIF};
        public final static IMAGE[] NO_GIF = new IMAGE[]{PNG, JPEG, WebP};
        
        private IMAGE(String regexPiece) {
            super(regexPiece);
        }
    }
    
    public static class MUSIC extends FileType {
        // Static Fields
        public final static MUSIC   MP3 = new MUSIC(".mp3");
        public final static MUSIC[] ALL = new MUSIC[]{MP3};
        
        private MUSIC(String regexPiece) {
            super(regexPiece);
        }
    }
    
    public static class VIDEO extends FileType {
        // Static Fields
        public final static VIDEO   MP4 = new VIDEO(".mp4");
        public final static VIDEO[] ALL = new VIDEO[]{MP4};
        
        private VIDEO(String regexPiece) {
            super(regexPiece);
        }
    }
}

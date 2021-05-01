package com.bridgelabz.employeepayroll;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class JavaWatcherServiceExample {
    private final WatchService watcher;
    private final Map<WatchKey, Path> dirWatcher;

    JavaWatcherServiceExample(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dirWatcher = new HashMap<WatchKey, Path>();
        scanAndRegisterDirectories(dir);
    }

    private void registerDirWatchers(Path dir) throws IOException {
        WatchKey key = dir.register(watcher,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
        dirWatcher.put(key,dir);
    }

    private void scanAndRegisterDirectories(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attribute) throws IOException {
                registerDirWatchers(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void processEvents() {
        while(true){
            WatchKey key;
            try{
                key = watcher.take();
            }catch (InterruptedException x){
                return;
            }
            Path dir = dirWatcher.get(key);
            if(dir == null)
                continue;
            for (WatchEvent<?> event : key.pollEvents()){
                WatchEvent.Kind kind = event.kind();
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
                System.out.format("%s: %s\n", event.kind().name(),child);

                if(kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child))
                            scanAndRegisterDirectories(child);
                    } catch (IOException e){}
                }
                else if (kind.equals(ENTRY_DELETE)){
                    if(Files.isDirectory(child))
                        dirWatcher.remove(key);
                }
            }

            boolean valid = key.reset();
            if(!valid){
                dirWatcher.remove(key);
                if(dirWatcher.isEmpty())
                    break;
            }
        }
    }
}

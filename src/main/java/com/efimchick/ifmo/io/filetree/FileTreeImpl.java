package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.*;


public class FileTreeImpl implements FileTree {

    static class TreeResult {
        private final String tree;
        private final long size;

        public TreeResult(String tree, long size) {
            this.tree = tree;
            this.size = size;
        }
    }

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || !path.toFile().exists()) {
            return Optional.empty();
        }
        File file = path.toFile();
        if (file.isFile()) {
            return Optional.of(printFileNameSize(file));
        }
        return Optional.of(buildTree(file, "").tree);
    }

    private String printFileNameSize(File file) {
        return file.getName() + " " + file.length() + " bytes";
    }

    private TreeResult buildTree(File directory, String indent) {
        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        files.sort((File a, File b) -> {
            int result = Boolean.compare(a.isFile(), b.isFile());
            if (result == 0) {
                result = a.getName().compareToIgnoreCase(b.getName());
            }
            return result;
        });
        long size = 0;
        StringBuilder treeSB = new StringBuilder();
        for (File file : files) {
            StringBuilder indentSB = new StringBuilder(indent);
            boolean isLast = file.equals(files.get(files.size() - 1));
            indentSB.append(isLast ? "   " : "│  ");
            treeSB.append("\n")
                    .append(indent).append(isLast ? "└─ " : "├─ ");
            if (file.isFile()) {
                size += file.length();
                treeSB.append(printFileNameSize(file));
            }
            if (file.isDirectory()) {
                TreeResult recursionTree = buildTree(file, indentSB.toString());
                size += recursionTree.size;
                treeSB.append(recursionTree.tree);
            }
        }
        return new TreeResult(directory.getName() + " " + size + " bytes" + treeSB, size);
    }
}

//public class FileTreeImpl implements FileTree {
//
//    @Override
//    public Optional<String> tree(Path path) {
//        File file = new File(String.valueOf(path));
//        if ( !file.exists()) return Optional.empty();
//        if ( file.isFile()) {
//            return Optional.of(file.getName() + " " + file.length() + " bytes");
//        }
//        if (file.isDirectory()) {
////            System.out.println(directoryTree(file, new ArrayList<>()));
//            return Optional.of(directoryTree(file, new ArrayList<>()));
//        }
//        return Optional.empty();
//    }
//    private String directoryTree(File folder, List<Boolean> lastFolders) {
//        String directory = "";
//        if (lastFolders.size() != 0)
//            directory += (!(lastFolders.get(lastFolders.size() -1 )) ? "├─ " : "└─ ");
//        directory += folder.getName() + " " + folderSize(folder);
//
//        File[] files = folder.listFiles();
//        int count = files.length;
//        files = sortFiles(files);
//        for (int i = 0; i < count; i++) {
//            directory += "\n";
//            for (Boolean lastFolder : lastFolders) {
//                if (lastFolder) {
//                    directory += "   ";
//                } else {
//                    directory += "│  ";
//                }
//            }
//            if (files[i].isFile()) {
//                directory += (i + 1 == count ? "└" : "├") + "─ " +
//                        files[i].getName() + " " + files[i].length() + " bytes";
//            } else {
//                ArrayList<Boolean> list = new ArrayList<>(lastFolders);
//                list.add(i+1 == count);
//                directory += directoryTree(files[i], list);
//            }
//        }
//        return directory;
//    }
//    private long getFolderSize(File folder) {
//        long size = 0;
//        File[] files = folder.listFiles();
//
//        for (File file : files) {
//            if (file.isFile()) {
//                size += file.length();
//            } else {
//                size += getFolderSize(file);
//            }
//        }
//        return size;
//    }
//    private String folderSize(File folder) {
//        return getFolderSize(folder) + " bytes";
//    }
//    private File[] sortFiles(File[] folder) {
//
//        Arrays.sort(folder);
//        List<File> sorted = new ArrayList<>();
//
//        for (File value : folder) {
//            if (value.isDirectory()) sorted.add(value);
//        }
//
//        for (File file : folder) {
//            if (file.isFile()) sorted.add(file);
//        }
//        return sorted.toArray(new File[sorted.size()]);
//    }
//}

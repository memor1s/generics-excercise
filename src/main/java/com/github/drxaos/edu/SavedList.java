package com.github.drxaos.edu;

import com.sun.deploy.util.SyncFileAccess;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class SavedList<E extends Serializable> extends AbstractList<E> implements Reloadable {

    private final File storageFile;
    private final List<E> cache = new ArrayList<>();

    public SavedList(@NotNull File file) {
        storageFile = file;
        internalReload();
    }

    public void reload() throws FileOperationException {
        internalReload();
    }

    @Override
    public E get(int index) {
        return cache.get(index);
    }

    @Override
    public E set(int index, E element) {
        E e = cache.set(index, element);
        internalSave();
        return e;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void add(int index, E element) {
        cache.add(index, element);
        internalSave();
    }

    @Override
    public E remove(int index) {
        E e = cache.remove(index);
        internalSave();
        return e;
    }

    private void internalReload() {
        cache.clear();
        if (storageFile.exists()) {
            try (ObjectInputStream loadStream = new ObjectInputStream(new FileInputStream(storageFile))) {
                cache.addAll((List) loadStream.readObject());
            } catch (ClassNotFoundException | IOException e) {
                throw new FileOperationException(e);
            }
        }
    }

    private void internalSave() {
        try (ObjectOutputStream saveStream = new ObjectOutputStream(new FileOutputStream(storageFile))) {
            saveStream.writeObject(cache);
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }
}



package com.apptoeat.utils;

import com.apptoeat.menus.WorldCreator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public final class WorldRepo {
    private static final CollectionReference COL =
            FirebaseFirestore.getInstance().collection("worlds");

    /* Save or overwrite -------------------------------------------- */
    public static Task<Void> save(WorldCreator world) {
        return COL.document(world.getName()).set(world);
    }

    /* Load all worlds ---------------------------------------------- */
    public static Task<List<WorldCreator>> fetchAll() {
        return COL.get().continueWith(task -> {
            List<WorldCreator> list = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    try  {
                        list.add(doc.toObject(WorldCreator.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return list;
        });
    }

    // WorldRepo.java
    public static Task<Void> delete(WorldCreator world) {
        return FirebaseFirestore
                .getInstance()
                .collection("worlds")
                .document(world.getName())
                .delete();
    }
}

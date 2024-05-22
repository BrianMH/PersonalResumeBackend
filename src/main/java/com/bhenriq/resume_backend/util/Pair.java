package com.bhenriq.resume_backend.util;

import lombok.ToString;

/**
 * Pair interface that is only used to transfer data between the service and control layers. Objects are final to prevent
 * any sort of odd interactions that might arise.
 * @param <L>
 * @param <R>
 */
@ToString
public class Pair<L, R>{
    public final L left;
    public final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}

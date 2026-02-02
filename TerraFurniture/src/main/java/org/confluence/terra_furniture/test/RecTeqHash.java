package org.confluence.terra_furniture.test;

import org.jetbrains.annotations.TestOnly;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ONLY USE FOR DEBUGGING, DO NOT REMOVE
 */
@TestOnly
public class RecTeqHash {
    public static final Set<Integer> hash = new HashSet<>();
    public static final AtomicBoolean isIn = new AtomicBoolean(false);
}

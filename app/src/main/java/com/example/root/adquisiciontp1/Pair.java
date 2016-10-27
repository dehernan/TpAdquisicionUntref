package com.example.root.adquisiciontp1;

import java.io.Serializable;

class Pair<L,R> implements Serializable{

    private final L left;
    private final R right;

    Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    L getLeft() { return left; }
    R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }
}
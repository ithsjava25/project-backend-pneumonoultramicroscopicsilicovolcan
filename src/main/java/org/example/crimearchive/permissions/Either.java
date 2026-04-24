package org.example.crimearchive.permissions;

public record Either<L, R>(L left, R right) {
    public static <L, R> Either<L, R> left(L value) { return new Either<>(value, null); }
    public static <L, R> Either<L, R> right(R value) { return new Either<>(null, value); }

    public boolean isLeft() { return left != null; }
    public boolean isRight() { return right != null; }
}
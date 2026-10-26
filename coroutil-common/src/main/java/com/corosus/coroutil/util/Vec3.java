package com.corosus.coroutil.util;

import java.util.Objects;

/**
 * Standard 3D vector used across CoroUtil for math, physics, and polygon checks.
 */
public class Vec3 {

    public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);

    public final double x;
    public final double y;
    public final double z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public double distanceTo(Vec3 vec) {
        double dx = vec.x - this.x;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceToSqr(Vec3 vec) {
        double dx = vec.x - this.x;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public Vec3 add(Vec3 vec) {
        return add(vec.x, vec.y, vec.z);
    }

    public Vec3 subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    public Vec3 subtract(Vec3 vec) {
        return subtract(vec.x, vec.y, vec.z);
    }

    public Vec3 multiply(double scale) {
        return new Vec3(this.x * scale, this.y * scale, this.z * scale);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vec3 normalize() {
        double len = length();
        return len < 1.0E-4 ? ZERO : new Vec3(this.x / len, this.y / len, this.z / len);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vec3 other)) return false;
        return Double.compare(other.x, x) == 0 &&
               Double.compare(other.y, y) == 0 &&
               Double.compare(other.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}

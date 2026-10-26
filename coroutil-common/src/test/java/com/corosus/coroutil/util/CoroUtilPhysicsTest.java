package com.corosus.coroutil.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoroUtilPhysicsTest {

    @Test
    void testIsInConvexShape() {
        List<Vec3> polygon = List.of(
                new Vec3(0, 0, 0),
                new Vec3(10, 0, 0),
                new Vec3(10, 0, 10),
                new Vec3(0, 0, 10)
        );

        Vec3 inside = new Vec3(5, 0, 5);
        Vec3 outside = new Vec3(15, 0, 5);

        assertTrue(CoroUtilPhysics.isInConvexShape(inside, polygon), "Point (5,5) should be inside polygon");
        assertFalse(CoroUtilPhysics.isInConvexShape(outside, polygon), "Point (15,5) should be outside polygon");
    }

    @Test
    void testDistBetweenPointAndLine() {
        double dist = CoroUtilPhysics.distBetweenPointAndLine(5, 5, 0, 0, 10, 0);
        assertEquals(5.0, dist, 0.001, "Distance to horizontal line at y=0 from (5,5) should be 5.0");
    }

    @Test
    void testVec3Math() {
        Vec3 v1 = new Vec3(1, 2, 3);
        Vec3 v2 = new Vec3(4, 6, 3);

        assertEquals(5.0, v1.distanceTo(v2), 0.001);
        assertEquals(25.0, v1.distanceToSqr(v2), 0.001);

        Vec3 added = v1.add(1, 1, 1);
        assertEquals(new Vec3(2, 3, 4), added);
    }
}

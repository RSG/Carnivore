package org.rsg.carnivore.ilan;

import processing.core.PApplet;

/////////////////////////////////////////////////////////////////////////////
// Vector3D -- holds three floats 
// used for flatspace vectors, volumetric vectors, and circles (where z is used for radius) 
public class Vector3D {
  public float x;
  public float y;
  public float z;

  public Vector3D(float radian) { 
    x = PApplet.cos(radian);
    y = PApplet.sin(radian);
    z = 0;
  }
  
  public Vector3D(float x_, float y_, float z_) {
    x = x_; y = y_; z = z_;
  }

  public Vector3D(float x_, float y_) {
    x = x_; y = y_; z = 0f;
  }
  
  Vector3D() {
    x = 0f; y = 0f; z = 0f;
  }

  public Vector3D(Vector3D v) {
    x = v.x; y = v.y; z = v.z;
  }
  
  void setX(float x_) {
    x = x_;
  }

  void setY(float y_) {
    y = y_;
  }

  void setZ(float z_) {
    z = z_;
  }
  
  public void setXY(float x_, float y_) {
    x = x_;
    y = y_;
  }
  
  void setXYZ(float x_, float y_, float z_) {
    x = x_;
    y = y_;
    z = z_;
  }

  void setXYZ(Vector3D v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }
  public float magnitude() {
    return (float) Math.sqrt(x*x + y*y + z*z);
  }

  public Vector3D copy() {
    return new Vector3D(x,y,z);
  }

  public Vector3D copy(Vector3D v) {
    return new Vector3D(v.x, v.y,v.z);
  }
  
  public void add(Vector3D v) {
    x += v.x;
    y += v.y;
    z += v.z;
  }

  public void sub(Vector3D v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
  }

  public void reflectX() {
    x = -x;
  }

  public void reflect() {
    x = -x;
    y = -y;
    z = -z;
  }

  public void add(float f) {
    x += f;
    y += f;
    z += f;
  }

  public void sub(float f) {
    x -= f;
    y -= f;
    z -= f;
  }

  public void mult(float n) {
    x *= n;
    y *= n;
    z *= n;
  }

  public void div(float n) {
    x /= n;
    y /= n;
    z /= n;
  }
  
  //a makes a unit vector (i.e. magnitude of 1)
  public void normalize() {
    float m = magnitude();
    if (m > 0) {
       div(m);
    }
  }
  
  public void normalize(float magnitude) {
    normalize();
    mult(magnitude);
  }

  public void limit(float max) {
    if (magnitude() > max) {
      normalize(max);
    }
  }

  public float heading2D() {
    float angle = (float) Math.atan2(-y, x);
    return -1*angle;
  }

  public Vector3D add(Vector3D v1, Vector3D v2) {
    Vector3D v = new Vector3D(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
    return v;
  }

  public Vector3D sub(Vector3D v1, Vector3D v2) {
    Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
    return v;
  }

  public Vector3D div(Vector3D v1, float n) {
    Vector3D v = new Vector3D(v1.x/n,v1.y/n,v1.z/n);
    return v;
  }

  public Vector3D mult(Vector3D v1, float n) {
    Vector3D v = new Vector3D(v1.x*n,v1.y*n,v1.z*n);
    return v;
  }

  public float distance (Vector3D v1, Vector3D v2) {
    float dx = v1.x - v2.x;
    float dy = v1.y - v2.y;
    float dz = v1.z - v2.z;
    return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
  }
  
  public String toString() {
    return "["+x+","+y+","+z+"]";
  }

}

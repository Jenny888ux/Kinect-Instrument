import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import KinectPV2.KJoint; 
import KinectPV2.*; 
import processing.serial.*; 
import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SkeletonTracking extends PApplet {






KinectPV2 kinect;
Skeleton[] skeletons;

Server myServer = new Server(this, 12345);
Serial port;

int[][] map;

public void setup() {
  
  kinect = new KinectPV2(this);
  kinect.enableSkeletonColorMap(true);
  kinect.enableColorImg(true);
  kinect.init();
  skeletons = new Skeleton[6];
  map = new int[16][16];
  for(int i=0; i<16; i++) {
    for(int j=0; j<16; j++) {
      map[i][j] = 0;
    }
  }
  for(int i=0; i<6; i++){
    skeletons[i] = new Skeleton();
  }
  // port = new Serial(this, "", 9600);
}

public void draw() {
  for(int i=0; i<16; i++) {
    for(int j=0; j<16; j++) {
      map[i][j] = 0;
    }
  }
  background(0);
  ArrayList<KSkeleton> skeletonArray = kinect.getSkeletonColorMap();
  for(int i = 0; i < skeletonArray.size(); i++) {
    KSkeleton skeleton = (KSkeleton) skeletonArray.get(i);
    if(skeleton.isTracked()) {
      KJoint[] joints = skeleton.getJoints();
      skeletons[i].updateSkeleton(makeJoints(joints));
      ArrayList<Point> points = skeletons[i].createMap();

      for(int j=0;j<points.size(); j++) {
        Point p = (Point) points.get(j);
        map[(int)p.y][(int)p.x] = 1;
      }
    }
  }
  String str = "";
  for(int i=0;i<16;i++) {
    for(int j=0;j<16;j++) {
      str += (String)map[i][j];
      str += "-";
    }
    str = str.substring(0, str.length()-1);
    str += ",";
  }
  str = str.substring(0, str.length()-1);
  myServer.write(str);
}
// void serialEvent(Serial p) {
//   String stringData = port.readStringUntil(10);
//   if(stringData!=null) {
//
//   }
// }
public Point[] makeJoints(KJoint[] joints) {
  Point[] points = {
    new Point(joints[KinectPV2.JointType_Head].getX(), joints[KinectPV2.JointType_Head].getY()),
    new Point(joints[KinectPV2.JointType_SpineShoulder].getX(), joints[KinectPV2.JointType_SpineShoulder].getY()),
    new Point(joints[KinectPV2.JointType_SpineBase].getX(), joints[KinectPV2.JointType_SpineBase].getY()),
    new Point(joints[KinectPV2.JointType_ShoulderRight].getX(), joints[KinectPV2.JointType_ShoulderRight].getY()),
    new Point(joints[KinectPV2.JointType_ElbowRight].getX(), joints[KinectPV2.JointType_ElbowRight].getY()),
    new Point(joints[KinectPV2.JointType_WristRight].getX(), joints[KinectPV2.JointType_WristRight].getY()),
    new Point(joints[KinectPV2.JointType_ShoulderLeft].getX(), joints[KinectPV2.JointType_ShoulderLeft].getY()),
    new Point(joints[KinectPV2.JointType_ElbowLeft].getX(), joints[KinectPV2.JointType_ElbowLeft].getY()),
    new Point(joints[KinectPV2.JointType_WristLeft].getX(), joints[KinectPV2.JointType_WristLeft].getY()),
    new Point(joints[KinectPV2.JointType_KneeRight].getX(), joints[KinectPV2.JointType_KneeRight].getY()),
    new Point(joints[KinectPV2.JointType_FootRight].getX(), joints[KinectPV2.JointType_FootRight].getY()),
    new Point(joints[KinectPV2.JointType_KneeLeft].getX(), joints[KinectPV2.JointType_KneeLeft].getY()),
    new Point(joints[KinectPV2.JointType_FootLeft].getX(), joints[KinectPV2.JointType_FootLeft].getY())
  };
  return points;
}
class Line {
  float a;
  float b;
  Point p1;
  Point p2;
  Line(Point _p1, Point _p2) {
    a = (_p2.y - _p1.y) / (_p2.x - _p1.x);
    b = _p1.y - a * _p1.x;
    p1 = new Point(p1.x, p1.y);
    p2 = new Point(p2.x, p2.y);
  }
  public float f(float x) {
    return a*x + b;
  }
}
class Point {
  float x;
  float y;
  Point(float _x, float _y) {
    x = _x;
    y = _y;
  }
  Point(int _x, int _y) {
    x = _x;
    y = _y;
  }
}
class Skeleton {
  Point[] joints;
  Line[] lines;
  Skeleton() {
    joints = new Point[13];
    lines = new Line[12];
  }
  public void updateSkeleton(Point[] _joints) {
    joints = _joints;
    updateLines();
  }
  public ArrayList<Point> createMap() {
    int W = width / 16;
    int H = height / 16;
    ArrayList<Point> map = new ArrayList();
    for(int i=0; i<12; i++) {
      Line l = lines[i];
      for(int j=1; j<=16; j++) {
        float x = l.p1.x + (l.p2.x - l.p1.x) * j/16.0f;
        float y = l.f(x);
        map.add(new Point(y / H,x / W));
      }
    }
    return map;
  }
  public void updateLines() {
    lines[0] = new Line(joints[0],joints[1]);
    lines[1] = new Line(joints[3],joints[1]);
    lines[2] = new Line(joints[6],joints[1]);
    lines[3] = new Line(joints[2],joints[1]);
    lines[4] = new Line(joints[3],joints[4]);
    lines[5] = new Line(joints[4],joints[5]);
    lines[6] = new Line(joints[6],joints[7]);
    lines[7] = new Line(joints[7],joints[8]);
    lines[8] = new Line(joints[2],joints[9]);
    lines[9] = new Line(joints[9],joints[10]);
    lines[10] = new Line(joints[2],joints[11]);
    lines[11] = new Line(joints[11],joints[12]);
  }
}
  public void settings() {  size(1000,500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SkeletonTracking" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

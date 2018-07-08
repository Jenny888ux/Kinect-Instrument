import KinectPV2.KJoint;
import KinectPV2.*;
import processing.serial.*;
import processing.net.*;

KinectPV2 kinect;
Skeleton[] skeletons;

Server myServer = new Server(this, 12345);
Serial port;

int[][] map;

void setup() {
  size(1000,500);
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

void draw() {
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
Point[] makeJoints(KJoint[] joints) {
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

class Skeleton {
  Point[] joints;
  Line[] lines;
  Skeleton() {
    joints = new Point[13];
    lines = new Line[12];
  }
  void updateSkeleton(Point[] _joints) {
    joints = _joints;
    updateLines();
  }
  ArrayList<Point> createMap() {
    int W = width / 16;
    int H = height / 16;
    ArrayList<Point> map = new ArrayList();
    for(int i=0; i<12; i++) {
      Line l = lines[i];
      for(int j=1; j<=16; j++) {
        float x = l.p1.x + (l.p2.x - l.p1.x) * j/16.0;
        float y = l.f(x);
        map.add(new Point(y / H,x / W));
      }
    }
    return map;
  }
  void updateLines() {
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

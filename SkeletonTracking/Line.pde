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
  float f(float x) {
    return a*x + b;
  }
}

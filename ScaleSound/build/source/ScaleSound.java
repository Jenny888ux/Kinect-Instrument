import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.ugens.*; 
import ddf.minim.effects.*; 
import ddf.minim.analysis.*; 
import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ScaleSound extends PApplet {






Client myClient = new Client(this, "127.0.0.1" ,12345);

Minim minim;
AudioSample[] players;
EchoClass echo;
AudioOutput out;
FFT fft;
int[][] map;
int count = 0;

public void setup() {
  
  // map\u3092\u521d\u671f\u5316\u3059\u308b \u3053\u308c\u304c\u30eb\u30fc\u30d7\u3055\u308c\u308b\u3088\uff01\uff01
  map = new int[16][16];
  for(int i=0; i<16; i++) {
    for(int j=0; j<16; j++) {
      if((int)random(7)==2) {
        map[i][j] = 1;
      } else {
        map[i][j] = 0;
      }
      // if(i==j) {
      //   map[i][j] = 1;
      // } else {
      //   map[i][j] = 0;
      // }
    }
  }
  // \u97f3\u58f0\u306e\u30ed\u30fc\u30c9
  minim = new Minim(this);
  players = new AudioSample[16];
  players[0] = minim.loadSample("1C.wav");
  players[1] = minim.loadSample("1D.wav");
  players[2] = minim.loadSample("1F.wav");
  players[3] = minim.loadSample("1G.wav");
  players[4] = minim.loadSample("2A.wav");
  players[5] = minim.loadSample("2C.wav");
  players[6] = minim.loadSample("2D.wav");
  players[7] = minim.loadSample("2F.wav");
  players[8] = minim.loadSample("2G.wav");
  players[9] = minim.loadSample("3A.wav");
  players[10] = minim.loadSample("3C.wav");
  players[11] = minim.loadSample("3D.wav");
  players[12] = minim.loadSample("3F.wav");
  players[13] = minim.loadSample("3G.wav");
  players[14] = minim.loadSample("4A.wav");
  players[15] = minim.loadSample("4C.wav");

  // echo = new EchoClass(44100.0, 0.5, 0.2, 2);
  // for(int i=0; i<16; i++) {
  //   //players[i].addEffect(echo);
  // }
  // for(int i=0; i<16; i++) {
  //    players[i].trigger();
  //    // if(i>1) players[i-2].close();
  //    delay(200);
  // }

  //\u9ad8\u901f\u30d5\u30fc\u30ea\u30a8\u5909\u63db\u306e\u6e96\u5099
  out = minim.getLineOut(Minim.STEREO,512,44100);
  fft = new FFT(out.bufferSize(), out.sampleRate());
  fft.window(FFT.HAMMING);
}

public void draw() {
  background(255);
  // 15\u30d5\u30ec\u30fc\u30e0\u3054\u3068\u306b\u97f3\u3092\u9cf4\u3089\u3059
  if(frameCount % 15 == 0) {
    for(int i=0; i<16; i++) {
      if(map[count][i] == 1) {
        players[i].trigger();
      }
    }
    count++;
    if(count>15) {
      count = 0;
    }
  }
  //\u9ad8\u901f\u30d5\u30fc\u30ea\u30a8\u5909\u63db \u30b3\u30fc\u30c9\u306e\u6982\u8981\u3060\u3051\u66f8\u3044\u305f\u306e\u3067\u6c17\u306b\u3057\u306a\u3044\u3067

  fft.forward(out.mix);
  for(int i=0; i<fft.specSize(); i++) {
    float x = map(i,0,fft.specSize(),0,width);
    line(x, 490, x, 490 - fft.getBand(i)*100);
  }
  // \u3053\u3053\u307e\u3067

  // window\u306e\u4e38\u305f\u3061\u3092\u8868\u793a
  // \u97f3\u304c\u306a\u308b\u97f3\u968e\u306f\u30d4\u30f3\u30af \u306a\u3089\u306a\u3044\u306e\u306f\u9ed2
  for(int i=0; i<16;i++) {
    for(int j=0; j<16; j++) {
      if(map[i][j] == 0) {
        fill(0);
        if(i==count) {
          fill(200);
        }
      } else {
        fill(220,100,150);
      }
      ellipse(i*30+15,(15-j)*30+15,30,30);
    }
  }
  /* Kinect\u3067\u3068\u3063\u305f\u753b\u50cf\u306e\u30c7\u30fc\u30bf\u3092\u30b5\u30fc\u30d0\u3092\u901a\u3057\u3066\u9001\u308b

  if(myClient.available() > 0) {
    String recieveStr = myClient.readString();
    String[] lines = recieveStr.split(",");
    for(int i=0; i<16; i++) {
      String[] chars = lines[i].split("-");
      for(int j=0;j<16; j++) {
        map[i][j] == (int)chars[j];
      }
    }
  }
  */
}
public void stop() {
  for(int i=0; i<16; i++) {
    players[i].close();
    minim.stop();
    super.stop();
  }
}
class EchoClass implements AudioEffect {
  float[] l_buffer;
  float[] r_buffer;
  int buffer_size;
  int l_index,r_index;
  float delay_time;
  int feedback;
  float[] delay_lebel;

  EchoClass(float fs, float dt, float dl, int fb) {
    delay_time = fs * dt;
    feedback = fb;
    buffer_size = (int)(delay_time * feedback) + 256;
    l_index = 0;
    r_index = 0;
    l_buffer = new float[buffer_size];
    r_buffer = new float[buffer_size];

    for(int i=0; i<buffer_size; i++) {
      l_buffer[i] = 0.0f;
      r_buffer[i] = 0.0f;
    }
    delay_lebel = new float[feedback];

    for(int i=0; i<feedback; i++) {
      delay_lebel[i] = pow(dl, (float)(i+i));
    }
  }

  public int echo_process(float[] samp, float[] buffer, int ix) {
    int index = ix;
    float[] out = new float[samp.length];
    for(int n=0; n<samp.length; n++) {
      buffer[index] = samp[n];
      float data = samp[n];
      for(int i=0; i<feedback; i++) {
        int m = index - (int)((i+1) * delay_time);
        if(m<0) {
          m+=buffer_size;
        }
        data += delay_lebel[i] * buffer[m];
      }
      out[n] = data;
      index++;
      if(index>=buffer_size) {
        index = 0;
      }
    }
    arraycopy(out, samp);
    return index;
  }

  public void process(float[] samp) {
    l_index = echo_process(samp,l_buffer, l_index);
  }

  public void process(float[] left, float[] right) {
    l_index = echo_process(left, l_buffer, l_index);
    r_index = echo_process(right, r_buffer, r_index);
  }
}
  public void settings() {  size(480,480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ScaleSound" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

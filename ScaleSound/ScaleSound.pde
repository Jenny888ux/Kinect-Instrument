import ddf.minim.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;
import ddf.minim.analysis.*;
import processing.net.*;
Client myClient = new Client(this, "127.0.0.1" ,12345);

Minim minim;
AudioSample[] players;
EchoClass echo;
AudioOutput out;
FFT fft;
int[][] map;
int count = 0;

void setup() {
  size(480,480);
  // mapを初期化する これがループされるよ！！
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
  // 音声のロード
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

  //高速フーリエ変換の準備
  out = minim.getLineOut(Minim.STEREO,512,44100);
  fft = new FFT(out.bufferSize(), out.sampleRate());
  fft.window(FFT.HAMMING);
}

void draw() {
  background(255);
  // 15フレームごとに音を鳴らす
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
  //高速フーリエ変換 コードの概要だけ書いたので気にしないで

  fft.forward(out.mix);
  for(int i=0; i<fft.specSize(); i++) {
    float x = map(i,0,fft.specSize(),0,width);
    line(x, 490, x, 490 - fft.getBand(i)*100);
  }
  // ここまで

  // windowの丸たちを表示
  // 音がなる音階はピンク ならないのは黒
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
  /* Kinectでとった画像のデータをサーバを通して送る

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
void stop() {
  for(int i=0; i<16; i++) {
    players[i].close();
    minim.stop();
    super.stop();
  }
}

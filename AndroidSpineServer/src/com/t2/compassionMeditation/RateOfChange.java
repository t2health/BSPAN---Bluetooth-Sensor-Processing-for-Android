 package com.t2.compassionMeditation;

 public class RateOfChange {
	 private float circularBuffer[];
	 	private float mean;
	 	private float instantChange;
        private int circularIndex;
        private int count;

        public RateOfChange(int size) {
            circularBuffer = new float[size];
            reset();
        }

        public float getValue() {
        	float total = 0;
        	int len = count < circularBuffer.length  ? count: circularBuffer.length;
        	for (int i = 0; i < len - 1; ++i) {
        		float v1 = circularBuffer[i];
        		float v2 = circularBuffer[nextIndex(i)];
        		float diff = Math.abs(v2 - v1);
        		total += diff;
            }

           float  roc = total / (float) len;
           return roc;
        }


        public void pushValue(float x) {
            if (count++ == 0) {
                primeBuffer(0);
            }
            float lastValue = circularBuffer[circularIndex];
            instantChange = x - lastValue;
            circularBuffer[circularIndex] = x;
            circularIndex = nextIndex(circularIndex);
        }

        public void reset() {
            count = 0;
            circularIndex = 0;
            mean = 0;
        }

        public long getCount() {
            return count;
        }

        private void primeBuffer(float val) {
            for (int i = 0; i < circularBuffer.length; ++i) {
                circularBuffer[i] = val;
            }
            mean = val;
        }

        private int nextIndex(int curIndex) {
            if (curIndex + 1 >= circularBuffer.length) {
                return 0;
            }
            return curIndex + 1;
        }
    }
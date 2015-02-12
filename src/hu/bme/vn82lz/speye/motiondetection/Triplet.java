package hu.bme.vn82lz.speye.motiondetection;

class Triplet<T, U, V> {
	T a;
	U b;
	V c;

	Triplet(T a, U b, V c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	T getA() {
		return a;
	}

	U getB() {
		return b;
	}

	V getC() {
		return c;
	}
}
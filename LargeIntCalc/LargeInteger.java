import java.util.Random;
import java.math.BigInteger;

public class LargeInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the LargeInteger by generating a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}

	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		LargeInteger product = karatsuba(this, other);

		return product;
	}

	public LargeInteger karatsuba(LargeInteger a, LargeInteger b) {
		// base case
		if(a.length() <= 1 && b.length() <= 1) return singleByte(a, b);

		a = a.checkFirst();
		b = b.checkFirst();

		// finds larger byte length of two LargeIntegers
		int n = Math.max(a.length(), b.length());
		int N = n * 4;

		LargeInteger x = a.newLargeInteger();
		LargeInteger y = b.newLargeInteger();

		LargeInteger xh = x.shiftRight(N).checkAll();
		LargeInteger newXh = xh.newLargeInteger();
		LargeInteger xSub = newXh.shiftLeft(N).checkAll();
		LargeInteger xl = a.checkNegative().subtract(xSub).checkAll();

		LargeInteger yh = y.shiftRight(N).checkAll();
		LargeInteger newYh = yh.newLargeInteger();
		LargeInteger ySub = newYh.shiftLeft(N).checkAll();
		LargeInteger yl = b.checkNegative().subtract(ySub).checkAll();

		LargeInteger M1 = karatsuba(xh, yh);
		LargeInteger M4 = karatsuba(xl, yl);
		LargeInteger M5 = karatsuba(xh.add(xl), yh.add(yl));

		return M4.add(M5.subtract(M1).subtract(M4).shiftLeft(N)).add(M1.shiftLeft(N * 2));
	}

	/**
	 * gets a new large integer with different reference
	 */
	public LargeInteger newLargeInteger() {
		byte[] arr = new byte[this.length()];

		for(int i = 0; i < arr.length; i++)
			arr[i] = this.getVal()[i];

		return new LargeInteger(arr);
	}

	/**
	 * if LargeIntegers can be represented as a single byte, multiplies bytes
	 * @param a	first large integer
	 * @param b	second large integer
	 * @return	product of a and b
	 */
	public LargeInteger singleByte(LargeInteger a, LargeInteger b) {
		if(a.length() == 0 || b.length() == 0) return new LargeInteger(new byte[]{(byte) 0});

		int aNum = twosComp(a.getVal()[0]);
		int bNum = twosComp(b.getVal()[0]);
		int product = aNum * bNum;

		byte[] result = new byte[2];
		result[0] = (byte) (product >> 8);
		result[1] = (byte) (product);

		return new LargeInteger(result).checkAll();
	}

	/**
	 * convert byte to twos comp
	 */
	public int twosComp(byte b) {
		if(b >= 0) return b;

		b &= 127;
		int i = b;
		i += 128;

		return i;
	}

	/**
	 * check negative
	 */
	public LargeInteger checkNegative() {
		if(!this.isNegative()) return this;

		byte[] arr = new byte[this.length() + 1];
		for(int i = 0; i < this.length(); i++)
			arr[i + 1] = this.getVal()[i];

		return new LargeInteger(arr);
	}

	/**
	 * check first
	 */
	public LargeInteger checkFirst() {
		if(this.length() == 1) return this;
		if(this.getVal()[0] != 0) return this;

		byte[] arr = new byte[this.length() - 1];
		for(int i = 0; i < arr.length; i++)
			arr[i] = this.getVal()[i + 1];

		return new LargeInteger(arr);
	}

	/**
	 *
	 * @return
	 */
	public int andFunc(int n) {
		int k = 1;
		for(int i = 0; i < n - 1; i++)
			k *= 2;
		return k;
	}

	/**
	 * shift larger integer right specified value
	 * @param shift	amount to shift
	 * @return	LargeInteger result
	 */
	public LargeInteger shiftRight(int shift) {
		if(shift == 0) return this;

		int div = shift / 8;
		shift = shift % 8;

		if(this.length() - div <= 0) return new LargeInteger(new byte[]{(byte) 0});

		byte[] arr = new byte[this.length() - div];

		for(int i = 0; i < arr.length; i++) arr[i] = this.getVal()[i];

		if(shift == 0)	return new LargeInteger(arr);

		byte carry = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] < 0) {
				arr[i] &= (byte) 127;
				carry += andFunc(8 - shift);
			}
			arr[i] >>>= shift;
			arr[i] += carry;
			carry = (byte) (this.getVal()[i] << (8 - shift));
		}

		return new LargeInteger(arr);
	}

	/**
	 * shift larger integer left specified value
	 * @param shift	amount to shift
	 * @return	LargeInteger result
	 */
	public LargeInteger shiftLeft(int shift) {
		if(shift == 0) return this;

		int div = shift / 8;
		shift = shift % 8;

		if(shift == 0) {
			byte[] arr = new byte[this.length() + div];

			for(int i = 0; i < this.length(); i++)
				arr[i] = this.getVal()[i];

			return new LargeInteger(arr);
		}

		byte[] arr = new byte[this.length()];
		for(int i = 0; i < arr.length; i++)
			arr[i] = this.getVal()[i];

		byte carry = 0;
		for(int i = arr.length - 1; i >= 0; i--) {
			int b = 0;
			if(arr[i] < 0) b = (byte) andFunc(shift);;
			arr[i] <<= shift;
			arr[i] += carry;

			carry = this.getVal()[i] &= (byte) 127;

			carry >>>= (8 - shift);
			carry += b;
		}

		byte[] temp = new byte[arr.length + div + 1];
		for(int i = 0; i < arr.length; i++)
			temp[i + 1] = arr[i];
		temp[0] += carry;

		return new LargeInteger(temp);
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other) {
		// returns 1, 1, 0 if this < other
 		if(this.subtract(other).isNegative()) return new LargeInteger[]{getBase(1), getBase(1), getBase(0)};
	 	// checks if both this = other or if other = 0
	 	if(this.checkEqual(other) || other.checkZero())
			return new LargeInteger[]{this, getBase(1), getBase(0)};

		LargeInteger[] val = this.divide(other, getBase(0));
	 	LargeInteger div = val[0].checkAll();
	 	LargeInteger mod = val[1].checkAll();

		LargeInteger[] array = other.XGCD(mod);
		LargeInteger g = array[0];
		LargeInteger x = array[2];

		LargeInteger y = div.multiply(array[2]);
		y = array[1].subtract(y);

		return new LargeInteger[]{g, x, y};
	 }

	/**
	 * @return LargeInteger array
	 * 		array[0] = this / other
	 * 		array[1] = this % other
	 */
	public LargeInteger[] divide(LargeInteger other, LargeInteger div) {
		LargeInteger x = this.newLargeInteger().checkAll();
		LargeInteger y = other.newLargeInteger().checkAll();

		if(x.length() == y.length()) {
			LargeInteger[] arr = x.baseDivide(y);
			return new LargeInteger[]{arr[0].add(div), arr[1]};
		}
		if(x.subtract(y).isNegative())
			return new LargeInteger[]{div, x};

		int xBits = (x.length() - 1) * 8 + x.sigBits();
		int yBits = (y.length() - 1) * 8 + y.sigBits();

		int diff = xBits - yBits;

		if(diff < 0) return new LargeInteger[]{x, getBase(0)};

		y = y.shiftLeft(diff).checkAll();

		if(x.subtract(y).isNegative()) {
			y = y.shiftRight(1);
			diff--;
		}

		div = div.add(getBase(2).shiftLeft(diff - 1));

		x = x.subtract(y).checkAll();

		LargeInteger[] array = x.divide(other, div);
		LargeInteger d = array[0].checkAll();
		LargeInteger m = array[1].checkAll();

		return new LargeInteger[]{d, m};
	}

	/**
	 * base case for divide
	 */
	public LargeInteger[] baseDivide(LargeInteger other) {
		LargeInteger x = this.newLargeInteger();
		LargeInteger y = other.newLargeInteger();

		LargeInteger div = getBase(0);

		while(!x.isNegative()) {
			x = x.subtract(y);
			div = div.add(getBase(1));
		}
		x = x.add(y);

		LargeInteger last = div.subtract(getBase(1));

		return new LargeInteger[]{last, x};
	}

	/**
	 * returns number of significant bits in most significant byte (for positive numbers)
	 */
	public int sigBits() {
		LargeInteger x = this.newLargeInteger().checkAll();
		if(x.isNegative()) return 0;

		byte b = this.getVal()[0];

		int c = 0;
		while(b != 0) {
			b >>= 1;
			c++;
			if(c >= 8) break;
		}

		return c;
	}

	/**
	 * checks first multiple times
	 */
	public LargeInteger checkAll() {
		LargeInteger x = this.newLargeInteger();
		for(int i = 0; i < x.length(); i++) {
			if(this.getVal()[i] == (byte) 0) x = x.checkFirst();
		}
		return x.checkNegative();
	}

	/**
	 * checks if a LargeInteger is equal to zero
	 */
	public boolean checkZero() {
		int zero = 0;
		for(int i = 0; i < this.length(); i++)
			if(this.getVal()[i] == (byte) 0) zero++;

		if(zero == this.length()) return true;

		return false;
	}

	/**
	 * checks if two LargeIntegers are equal
	 */
	public boolean checkEqual(LargeInteger other) {
		if(this.length() != other.length()) return false;

		for(int i = 0; i < this.length(); i++)
			if(this.getVal()[i] != other.getVal()[i]) return false;

		return true;
	}

	/**
	 * returns a LargeInteger of value i, must be less than Byte.MAX
	 * @param i	byte value
	 * @return LargeInteger of value i
	 */
	public LargeInteger getBase(int i) {
		return new LargeInteger(new byte[]{(byte) i});
	}

	/**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	 public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
	 	if(y.checkZero()) return getBase(1);

	 	LargeInteger b = y.newLargeInteger().checkAll();
		b = b.shiftRight(1);

		LargeInteger exp = this.modularExp(b, n).checkAll();
		LargeInteger square = (exp.multiply(exp)).divide(n, getBase(0))[1].checkAll();

		LargeInteger mod = y.checkAll().divide(getBase(2), getBase(0))[1].checkAll();
		 if(mod.checkOne(1)) {
		 	square = (square.multiply(this).checkAll()).divide(n, getBase(0))[1].checkAll();
		 }

		return square;
	 }

	/**
	 * checks if a LargeInteger = 1 or 2
	 * @param n num to check with
	 */
	public boolean checkOne(int n) {
		LargeInteger x = this.newLargeInteger().checkAll();

		if(x.getVal()[0] == n) return true;

		return false;
	}
}

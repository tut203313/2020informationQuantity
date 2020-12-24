package s4.B203313;
import java.lang.*;
import s4.specification.*;

/*package s4.specification;
�����́A1��A2��ƕύX�̂Ȃ��O���d�l�ł���B
public interface FrequencerInterface { // This interface provides the design for
	frequency counter.
	void setTarget(byte target[]); // set the data to search.
	void setSpace(byte space[]); // set the data to be searched target from.
	int frequency(); //It return -1, when TARGET is not set or TARGET's length is zero
	//Otherwise, it return 0, when SPACE is not set or SPACE's length is zero
	//Otherwise, get the frequency of TAGET in SPACE
	int subByteFrequency(int start, int end);
	// get the frequency of subByte of taget, i.e target[start], taget[start+1], ... ,
	target[end-1].
	// For the incorrect value of START or END, the behavior is undefined.
}
*/

public class Frequencer implements FrequencerInterface{
	// Code to start with: This code is not working, but good start point to work.
	byte [] myTarget;
	byte [] mySpace;

	boolean targetReady = false;
	boolean spaceReady = false;
	int [] suffixArray; // Suffix Array �̎����Ɏg���f�[�^�̌^�� int []�Ƃ���B

	// The variable, "suffixArray" is the sorted array of all suffixes of mySpace.
	// Each suffix is expressed by a integer, which is the starting position in mySpace.
	// The following is the code to print the contents of suffixArray.
	// This code could be used on debugging.
	private void printSuffixArray() {
		if(spaceReady) {
			for(int i=0; i< mySpace.length; i++) {
				int s = suffixArray[i];
				for(int j=s;j<mySpace.length;j++) {
					System.out.write(mySpace[j]);
				}
				System.out.write('\n');
			}
		}
	}

	private int suffixCompare(int i, int j) {
		// suffixCompare �̓\�[�g�̂��߂̔�r���\�b�h�ł���B
		// ���̂悤�ɒ�`����B
		// comparing two suffixes by dictionary order.
		// suffix_i is a string starting with the position i in "byte [] mySpace".
		// Each i and j denote suffix_i, and suffix_j.
		// Example of dictionary order
		// "i" < "o" : compare by code
		// "Hi" < "Ho" ; if head is same, compare the next element
		// "Ho" < "Ho " ; if the prefix is identical, longer string is big
		//

		//The return value of "int suffixCompare" is as follows.
		// if suffix_i > suffix_j, it returns 1
		// if suffix_i < suffix_j, it returns -1
		// if suffix_i = suffix_j, it returns 0;

		// �����ɃR�[�h���L�q����
		//
		if (mySpace[i] > mySpace[j]) return 1;
		else if (mySpace[i] < mySpace[j]) return -1;
		else if (i+1 == mySpace.length && j+1 == mySpace.length) return 0;
		else if (i+1 == mySpace.length) return -1;
		else if (j+1 == mySpace.length) return 1;
		else return suffixCompare(i+1, j+1);

		//return 0; // ���̍s�͕ύX���Ȃ���΂����Ȃ��B
	}

	public void setSpace(byte []space) {
		// suffixArray �̑O�����́AsetSpace �Œ�`����B
		mySpace = space; if(mySpace.length>0) spaceReady = true;
		// First, create unsorted suffix array.
		suffixArray = new int[space.length];
		// put all suffixes in suffixArray.

		for(int i = 0; i< space.length; i++) {
			suffixArray[i] = i; // Please note that each suffix is expressed by one	integer.
		}
		//
		// �����ɁAint suffixArray ���\�[�g����R�[�h�������B
		// ���Ԃ� suffixCompare �Œ�`�������̂Ƃ���B

		// bubble sort
		/*
		for (int i=0; i<suffixArray.length; i++){
			for (int j=suffixArray.length-1; j>i; j--){
				if(suffixCompare(suffixArray[i], suffixArray[j]) == 1){
					int num = suffixArray[i];
					suffixArray[i] = suffixArray[j];
					suffixArray[j] = num;
				}
			}
		}
		*/
		
		// quick sort
		quicksort(0, suffixArray.length-1);
	}

	// �N�C�b�N�\�[�g
	private void quicksort(int left, int right) {
		int i, pivot;
		int temp;

		// �I������
		if (left >= right) return;

		// ����ւ�
		pivot = left;
		for (i=left+1; i <= right; i++){
			if (suffixCompare(suffixArray[i], suffixArray[left]) == -1){
				pivot++;
				temp=suffixArray[pivot];
				suffixArray[pivot]=suffixArray[i];
				suffixArray[i]=temp;
			}
		}
		temp=suffixArray[left];
		suffixArray[left]=suffixArray[pivot];
		suffixArray[pivot]=temp;

		// ���E�̃\�[�g
		quicksort(left, pivot-1);
		quicksort(pivot+1, right);
	}

	// Suffix Array ��p���āA������̕p�x�����߂�R�[�h
	// ��������A�w�肷��͈͂̃R�[�h�͕ύX���Ă͂Ȃ�Ȃ��B
	public void setTarget(byte [] target) {
		myTarget = target; if(myTarget.length>0) targetReady = true;
	}
	public int frequency() {
		if(targetReady == false) return -1;
		if(spaceReady == false) return 0;
		return subByteFrequency(0, myTarget.length);
	}

	public int subByteFrequency(int start, int end) {
		/* This method be work as follows, but much more efficient
		int spaceLength = mySpace.length;
		int count = 0;
		for(int offset = 0; offset< spaceLength - (end - start); offset++) {
			boolean abort = false;
			for(int i = 0; i< (end - start); i++) {
				if(myTarget[start+i] != mySpace[offset+i]) { abort = true; break; }
			}
			if(abort == false) { count++; }
		}
		*/

		int first = subByteStartIndex(start, end);
		int last1 = subByteEndIndex(start, end);
		return last1 - first;
	}

	// �ύX���Ă͂����Ȃ��R�[�h�͂����܂ŁB

	private int targetCompare(int i, int j, int k) {
		// suffixArray ��T������Ƃ��Ɏg����r�֐��B
		// ���̂悤�ɒ�`����
		// suffix_i is a string in mySpace starting at i-th position.
		// target_i_k is a string in myTarget start at j-th postion ending k-th position.
		// comparing suffix_i and target_j_k.
		// if the beginning of suffix_i matches target_i_k, it return 0.
		// The behavior is different from suffixCompare on this case.
		// if suffix_i > target_i_k it return 1;
		// if suffix_i < target_i_k it return -1
		// It should be used to search the appropriate index of some suffix.

		// Example of search
		// suffix target
		// "o" > "i"
		// "o" < "z"
		// "o" = "o"
		// "o" < "oo"
		// "Ho" > "Hi"
		// "Ho" < "Hz"
		// "Ho" = "Ho"
		// "Ho" < "Ho " : "Ho " is not in the head of suffix "Ho"
		// "Ho" = "H" : "H" is in the head of suffix "Ho"
		//
		// �����ɔ�r�̃R�[�h������
		//
		if (mySpace[i] > myTarget[j]) return 1;
		else if (mySpace[i] < myTarget[j]) return -1;
		else if (j+1 == k) return 0;
		else if (i+1 == mySpace.length) return -1;
		else return targetCompare(i+1, j+1, k);
		//return 0; // ���̍s�͕ύX���Ȃ���΂Ȃ�Ȃ��B
	}

	private int subByteStartIndex(int start, int end) {
		//suffix array �̂Ȃ��ŁA�ړI�̕�����̏o�����n�܂�ʒu�����߂郁�\�b�h
		// �ȉ��̂悤�ɒ�`����B
		/* Example of suffix created from "Hi Ho Hi Ho"
		0: Hi Ho
		1: Ho
		2: Ho Hi Ho
		3:Hi Ho
		4:Hi Ho Hi Ho
		5:Ho
		6:Ho Hi Ho
		7:i Ho
		8:i Ho Hi Ho
		9:o
		A:o Hi Ho
		*/
		// It returns the index of the first suffix
		// which is equal or greater than target_start_end.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 5.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho ", it will return 6.
		//
		// �����ɃR�[�h���L�q����B
		//
		
		// First Pattern
		/*
		for (int i=0;i<suffixArray.length;i++){
			if(targetCompare(suffixArray[i], start, end) == 0) {
				return i;
			}
		}
		return -1;
		*/

		// binary search
		int left, right, middle, before;
		left = 0;
		right = suffixArray.length - 1;
		while(left <= right) {
			middle = (left + right) / 2;
			switch(targetCompare(suffixArray[middle], start, end)) {
			case 0:  //middle[] = target
				before = middle - 1;
				if (before < 0) return middle;
				if (targetCompare(suffixArray[before], start, end) != 0) {
					return middle;
				}
				right = middle - 1;
				break;
			case 1:  //middle[] > target
				right = middle - 1;
				break;
			case -1: //middle[] < target
				left = middle + 1;
				break;
			}
        }
		return -1;
		
		//return suffixArray.length; //���̃R�[�h�͕ύX���Ȃ���΂Ȃ�Ȃ��B
	}

	private int subByteEndIndex(int start, int end) {
		//suffix array �̂Ȃ��ŁA�ړI�̕�����̏o�����Ȃ��Ȃ�ꏊ�����߂郁�\�b�h
		// �ȉ��̂悤�ɒ�`����B
		/* Example of suffix created from "Hi Ho Hi Ho"
		0: Hi Ho
		1: Ho
		2: Ho Hi Ho
		3:Hi Ho
		4:Hi Ho Hi Ho
		5:Ho
		6:Ho Hi Ho
		7:i Ho
		8:i Ho Hi Ho
		9:o
		A:o Hi Ho
		*/
		// It returns the index of the first suffix
		// which is greater than target_start_end; (and not equal to target_start_end)
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 7 for "Hi Ho Hi Ho".

		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is"i", it will return 9 for "Hi Ho Hi Ho".
		//
		// �����ɃR�[�h���L�q����
		//

		// First pattern
		/*
		for (int i=suffixArray.length-1;i >= 0;i--){
			if(targetCompare(suffixArray[i], start, end) == 0) {
				return i+1;
			}
		}
		return -1;
		*/

		// binary search
		int left, right, middle, after;
		left = 0;
		right = suffixArray.length - 1;
		while(left <= right) {
			middle = (left + right) / 2;
			switch(targetCompare(suffixArray[middle], start, end)) {
			case 0:  //middle[] = target
				after = middle + 1;
				if (after > suffixArray.length - 1) return middle + 1;
				if (targetCompare(suffixArray[after], start, end) != 0) {
					return middle + 1;
				}
				left = middle + 1;
				break;
			case 1:  //middle[] > target
				right = middle - 1;
				break;
			case -1: //middle[] < target
				left = middle + 1;
				break;
			}
        }
		return -1;
		
		//return suffixArray.length; // ���̍s�͕ύX���Ȃ���΂Ȃ�Ȃ��A
	}
	// Suffix Array ���g�����v���O�����̃z���C�g�e�X�g�́A
	// private �ȃ��\�b�h�ƃt�B�[���h���A�N�Z�X���邱�Ƃ��K�v�Ȃ̂ŁA
	// �N���X�ɑ����� static main �ɏ������@������B
	// static main �������Ă��A�Ăт����Ȃ���΂悢�B
	// �ȉ��́A���R�ɕύX���Ď������邱�ƁB
	// ����:�W���o�́A�G���[�o�͂Ƀ��b�Z�[�W���o�����Ƃ́A
	// static main ����̎��s�̂Ƃ������ɋ������B
	// �O������ Frequencer ���g���Ƃ��Ƀ��b�Z�[�W���o�͂��Ă͂Ȃ�Ȃ��B
	// �����̃e�X�g���s�̂Ƃ��Ƀ��b�Z�[�W���ł�ƁA�d�l�ɂȂ����������Ƃ݂Ȃ��A
	// ���_�̑Ώۂł���B
	public static void main(String[] args) {
		Frequencer frequencerObject;
		try {
			frequencerObject = new Frequencer();
			frequencerObject.setSpace("Hi Ho Hi Ho".getBytes());
			frequencerObject.printSuffixArray(); // you may use this line for DEBUG
			/* Example from "Hi Ho Hi Ho"
			0:  Hi Ho
			1:  Ho
			2:  Ho Hi Ho
			3: Hi Ho
			4: Hi Ho Hi Ho
			5: Ho
			6: Ho Hi Ho
			7: i Ho
			8: i Ho Hi Ho
			9: o
			A: o Hi Ho
			*/

			// you may use this line for DEBUG
			//
			// **** Please write code to check subByteStartIndex, and subByteEndIndex
			//
			//test1 : Start "Hi Ho Hi Ho", "Ho", -> 5
			frequencerObject.setTarget("Ho".getBytes());
			System.out.println("StartTest1: " + frequencerObject.subByteStartIndex(0,2));
			//test2 : Start "Hi Ho Hi Ho", "Ho ", -> 6
			frequencerObject.setTarget("Ho ".getBytes());
			System.out.println("StartTest2: " + frequencerObject.subByteStartIndex(0,3));
			//test3 : End "Hi Ho Hi Ho", "Ho", -> 7
			frequencerObject.setTarget("Ho".getBytes());
			System.out.println("EndTest1: " + frequencerObject.subByteEndIndex(0,2));
			//test4 : End  "Hi Ho Hi Ho", "Ho", -> 9
			frequencerObject.setTarget("i".getBytes());
			System.out.println("EndTest2: " + frequencerObject.subByteEndIndex(0,1));

			frequencerObject.setTarget("H".getBytes());
		
			int result = frequencerObject.frequency();
			System.out.print("Freq = "+ result+" ");
			if(4 == result) { System.out.println("OK"); }
			else			{ System.out.println("WRONG"); }
		}
		catch(Exception e) {
			System.out.println("STOP");
			System.out.println(e);
		}
	}
}

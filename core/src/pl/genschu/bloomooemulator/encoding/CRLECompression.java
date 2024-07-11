package pl.genschu.bloomooemulator.encoding;

public class CRLECompression
{
    static byte[] resizeByteArr(byte[] arr, int newlength) {
        byte[] newArr = new byte[newlength];

        for(int i = 0; i < (Math.min(newlength, arr.length)); i++)
            newArr[i] = arr[i];

        return newArr;
    }

    /*that function insert subarray into array and resize it if it's needed, but it overrides bytes when subarray is inserted in the middle*/
    static byte[] insertSubArr(byte[] source, byte[] destination, int beg, int end, int offset) {
        if(offset+(end-beg) >= destination.length) destination = resizeByteArr(destination, offset+(end-beg));
        for(int i = offset; i < offset + (end - beg); i++) {
            destination[i] = source[beg + (i - offset)];
        }
        return destination;
    }

    static int byteToInt(byte bajt) {
        return bajt & 0xff;
    }

    public static byte[] decodeCRLE(byte[] data) {
        return decodeCRLE(data, 1);
    }

    public static byte[] decodeCRLE(byte[] data, int bulk) {
        byte[] n = new byte[0];
        int i=0;
        while(i<data.length){
            if(byteToInt(data[i])<128){
                n = insertSubArr(data, n, i+1, i+byteToInt(data[i])*bulk+1, n.length);
                i+=byteToInt(data[i])*bulk+1;
            }else{
                int helper = byteToInt(data[i]) - 128;
                int var=n.length;
                n = resizeByteArr(n, n.length+helper*bulk);

                for(int k=0;k<helper;k++){
                    for(int l=0;l<bulk;l++){
                        n[var+k*bulk+l] = data[i+l+1];
                    }
                }
                i+=1+bulk;
            }
        }
        return n;
    }
}

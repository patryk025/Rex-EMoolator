package pl.cba.genszu.amcodetranslator.encoding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/*
	INFORMACJA: ten kod nie działa dla każdego przypadku
	więc na chwilę obecną dekodowanie obrazków nie działa,
	na kilku testach przy bardziej złożonych lub dłuższych
	danych kod się wykłada
 */

class CLZW2Compression
 {
	protected int p1; //+0
	protected int p2; //+4
	protected int p3; //+8
	protected int p4; //+12
	protected int input_size; //+16
	protected char[] input_ptr; //+20, pointer
	protected int p7; //+24
	protected int p8; //+28
	
	//public String 
	
	public CLZW2Compression(char[] data) {
		this.input_size = data.length;
		this.input_ptr = data;
	}
	
	

	public int getSize(int offset) {
		int size = 0;
		for(int i = 0; i < 4; i++) {
			size += this.input_ptr[offset * 4 + i] * Math.pow(256, i);
		}
		return size;
	}

	 private void zeroSize(int offset) {
		 for(int i = 0; i < 4; i++) {
			 this.input_ptr[offset * 4 + i] = 0;
		 }
	 }

	 private void setSize(int offset, int size) {
		 /*for(int i = 0; i < 4; i++) {
			 this.input_ptr[offset * 4 + i] = 0;
		 }*/
		 int tmpVal;
		 for(int i = 3; i >= 0; i--) {
		 	tmpVal = (int) (size/Math.pow(256, i));
		 	size -= tmpVal*Math.pow(256, i);
		 	this.input_ptr[i*offset] = (char) tmpVal;
		 }
	 }

	 public void print_beautiful()
	 {
		 System.out.print("Obiekt klasy CLZW2Compression: ");
		 System.out.print(this);
		 System.out.print('\n');
		 System.out.print(" (int)    dlugosc: ");
		 System.out.print(input_size);
		 System.out.print('\n');
		 System.out.print(" (char *) dane:    ");
		 //System.out.print(input_ptr);
		 if (input_size <= 512) {
			 System.out.print(new String(input_ptr));
		 } else {
			 System.out.print(new String(input_ptr).substring(0, 510)+"(...)");
		 }
		 System.out.print('\n');
		 System.out.print("     pierwszy znak: ");
		 System.out.print(input_ptr[0]);
		 System.out.print('\n');
		 System.out.print("\n");
	 }

	 /*DEBUG*/
	 private char[] wzorzec;
	 private boolean debug = false;
	 //private boolean error = false;
	 
	 public void addOriginalContent(char[] content) {
		 this.wzorzec = content;
		 debug = true;
	 }

	 public char[] compress() throws Exception {
		throw new Exception("Not implemented");
		//return null;
	}
	public char[] decompress() throws Exception {
		/*
		 *  Adnotacja:
		 *  zmiana prior_decompress_ptr na decompress_ptr
		 *  
		 */
		 
		/*
		 *  TODO:
		 *  - optimize code
		 *  - remove redundant variables
		 *  - simplify if instructions
		 *  - fix current_byte >= 64
		 */
		 
		FileOutputStream fos = new FileOutputStream(new File("F:\\Documents and Settings\\Patryk\\Desktop\\śmietnisko 03-05-2020\\śmieci (1)\\AMEngineTest\\clzw_testy\\logi\\debugJava.csv"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		if (input_size > 8 && input_ptr != null) {
			//TODO: unsigned values
			//unsigned decompressed_size = reinterpret_cast<unsigned *>(input_ptr)[0];
			/*unsigned*/
			int decompressed_size = /*input_ptr[0]*/ getSize(0); //rozmiar po dekompresji
			char[] decompressed_ptr = new char[decompressed_size]; //miejsce na dekompresję
			//unsigned compressed_size = reinterpret_cast<unsigned *>(input_ptr)[1];              
			int compressed_size = /*input_ptr[1]*/getSize(1); //rozmiar skompresowany
			//unsigned char *compressed_ptr = reinterpret_cast<unsigned char *>(input_ptr + 8);   
			char[] compressed_ptr = Arrays.copyOfRange(input_ptr, 8, input_ptr.length); //dane skompresowane

			zeroSize(0); //zerowanie rozmiaru po dekompresji (czemu???)

			//te instrukcje przepisuję na styl Javovy
			/*unsigned char *last_compressed_ptr = reinterpret_cast<unsigned char *>(input_ptr + input_size); //ebp
			unsigned char *last_decompressed_ptr = reinterpret_cast<unsigned char *>(decompressed_ptr + decompressed_size);
			char *first_compressed_ptr = compressed_ptr;
			char *first_decompressed_ptr = decompressed_ptr; //ebx*/

			int last_compressed_ptr = input_size - 8; //odjęcie nagłówka
			int last_decompressed_ptr = decompressed_size;
			int first_compressed_ptr = 0;
			int first_decompressed_ptr = 0;

			/*unsigned*/
			char current_byte = compressed_ptr[0];
			/*unsigned*/
			int current_word = 0;
			/*unsigned*/
			long current_dword = 0;

			/*unsigned*/
			int i = 0;
			int compressCounter = 0;
			int decompressCounter = 0;
			int priorDecompressCounter = 0;
			int numerPetli = 0;

			bw.write("start, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
			while (compressCounter < input_size) {
				numerPetli++;
				if (((compressCounter - first_compressed_ptr == 0) && current_byte <= 17) || current_byte == 0) {
					//↓loc_100EAAE2
					current_byte = compressed_ptr[compressCounter];
					compressCounter++;
					if (current_byte < 16) {
						current_dword = current_byte;
						if (current_byte == 0) {
							if (compressCounter == 0) {
								do {//↓loc_100EAAF9
									current_byte = compressed_ptr[1]; //do sprawdzenia
									current_dword += 255; //0xFF
									compressCounter++;
								} while (current_byte == 0);
							}
							current_byte = compressed_ptr[compressCounter];
							compressCounter++;//↓loc_100EAB07
							current_dword += current_byte + 15;
						}
						i = (int) current_dword;//↓loc_100EAB10
						i += 3;
						bw.write("if zaraz za początkiem pętli -> current_byte < 16, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						
						for (; i > 0; i--) {//↓loc_100EAB38 a.k.a. loc_100EAB43
							if(debug) {
								if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
									System.out.println("Błąd dekodowania. Dane do debugowania: ");
									System.out.println("compressCounter="+compressCounter);
									System.out.println("decompressCounter="+decompressCounter);
									System.out.println("priorDecompressCounter="+priorDecompressCounter);
									System.out.println("current_byte="+(int) current_byte);
									System.out.println("current_word="+current_word);
									System.out.println("current_dword="+current_dword);
									System.out.println("i="+i);
									System.out.println("numerPetli="+numerPetli);
									System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
									for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
										System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
									}
									System.out.println(" |");
									bw.write("if zaraz za początkiem pętli -> current_byte < 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
									bw.close();
									//error=true;
									throw new CLZW2Exception("Błąd dekompresji. Szczegóły wyżej");
								}
							}
							decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
							compressCounter++;
							decompressCounter++;
						}
						//w tym momencie i = 0
						current_byte = compressed_ptr[compressCounter];//↓loc_100EAB4C
						compressCounter++;
						bw.write("if zaraz za początkiem pętli -> current_byte < 16, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
					}
					bw.write("if zaraz za początkiem pętli, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
				} else if (compressCounter - first_compressed_ptr == 0) {
					current_byte = compressed_ptr[compressCounter];
					compressCounter++;
					current_byte -= 17;
					for (i = current_byte; i > 0; i--) {
						if(debug) {
							if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
								System.out.println("Błąd dekodowania. Dane do debugowania: ");
								System.out.println("compressCounter="+compressCounter);
								System.out.println("decompressCounter="+decompressCounter);
								System.out.println("priorDecompressCounter="+priorDecompressCounter);
								System.out.println("current_byte="+(int) current_byte);
								System.out.println("current_word="+current_word);
								System.out.println("current_dword="+current_dword);
								System.out.println("i="+i);
								System.out.println("numerPetli="+numerPetli);
								System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
								for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
									System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
								}
								System.out.println(" |");
								bw.write("else dla ifa zaraz za początkiem pętli (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
								bw.close();
								//error=true;
								throw new CLZW2Exception("Błąd dekompresji");
							}
						}
						decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
						compressCounter++;
						decompressCounter++;
					}
					current_byte = compressed_ptr[compressCounter];//↓loc_100EAB4C
					compressCounter++;
					bw.write("else dla ifa zaraz za początkiem pętli, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
				}

				if (current_byte < 16) {
					/*current_byte >>= 2;
					/*unsigned*/
					//char[] prior_decompressed_ptr = Arrays.copyOfRange(decompressed_ptr, 0, decompressed_ptr.length);
					priorDecompressCounter = decompressCounter;
					priorDecompressCounter -= current_byte;
					current_word = compressed_ptr[compressCounter];
					current_word <<= 2;
					priorDecompressCounter -= current_word;
					priorDecompressCounter -= 2049; //wait WAT??? No to zajebiście, wyjątek jak nic
					compressCounter++;
					decompressCounter = priorDecompressCounter;
					compressCounter++;
					priorDecompressCounter++;

					if(debug) {
						if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
							System.out.println("Błąd dekodowania. Dane do debugowania: ");
							System.out.println("compressCounter="+compressCounter);
							System.out.println("decompressCounter="+decompressCounter);
							System.out.println("priorDecompressCounter="+priorDecompressCounter);
							System.out.println("current_byte="+(int) current_byte);
							System.out.println("current_word="+current_word);
							System.out.println("current_dword="+current_dword);
							System.out.println("i="+i);
							System.out.println("numerPetli="+numerPetli);
							System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
							for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
								System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
							}
							System.out.println(" |");
							bw.write("current_byte < 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							bw.close();
							//error=true;
							throw new CLZW2Exception("Błąd dekompresji");
						}
					}
					decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];//↓loc_100EAB77
					decompressCounter++;
					if(debug) {
						if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
							System.out.println("Błąd dekodowania. Dane do debugowania: ");
							System.out.println("compressCounter="+compressCounter);
							System.out.println("decompressCounter="+decompressCounter);
							System.out.println("priorDecompressCounter="+priorDecompressCounter);
							System.out.println("current_byte="+(int) current_byte);
							System.out.println("current_word="+current_word);
							System.out.println("current_dword="+current_dword);
							System.out.println("i="+i);
							System.out.println("numerPetli="+numerPetli);
							System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
							for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
								System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
							}
							System.out.println(" |");
							bw.write("current_byte < 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							bw.close();
							//error=true;
							throw new CLZW2Exception("Błąd dekompresji");
						}
					}
					decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter + 1];
					decompressCounter++;

					current_byte = compressed_ptr[compressCounter - 2];//↓loc_100EAB82
					current_byte &= 3;
					bw.write("current_byte < 16, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
				}

				if (current_byte > 0) {
					//↓loc_100EAB8E
					/*pętla do wywalenia*/
					for (; i > 0; i--) {
						if(debug) {
							if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
								System.out.println("Błąd dekodowania. Dane do debugowania: ");
								System.out.println("compressCounter="+compressCounter);
								System.out.println("decompressCounter="+decompressCounter);
								System.out.println("priorDecompressCounter="+priorDecompressCounter);
								System.out.println("current_byte="+(int) current_byte);
								System.out.println("current_word="+current_word);
								System.out.println("current_dword="+current_dword);
								System.out.println("i="+i);
								System.out.println("numerPetli="+numerPetli);
								System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
								for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
									System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
								}
								System.out.println(" |");
								bw.write("current_byte > 0 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
								bw.close();
								//error=true;
								throw new CLZW2Exception("Błąd dekompresji");
							}
						}
						decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
						compressCounter++;
						decompressCounter++;
					}
					bw.write("current_byte > 0, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
					//current_byte = *compressed_ptr;
					//compressed_ptr++;

					;//↓loc_100EAB9C
					if (current_byte >= 64) {
						i = current_byte;
						/*unsigned*/
						//char[] prior_decompressed_ptr = Arrays.copyOfRange(decompressed_ptr, 0, decompressed_ptr.length);
						priorDecompressCounter = decompressCounter;
						current_byte >>= 2;
						current_byte &= 7;
						priorDecompressCounter -= current_byte;
						current_word = compressed_ptr[compressCounter];
						compressCounter++;
						current_word <<= 3;
						priorDecompressCounter -= current_word;
						priorDecompressCounter--;
						i >>= 5;

						//↓loc_100EABBC
						i++;

						bw.write("current_byte > 0 -> current_byte >= 64, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");

						//↓loc_100EABC9
						for (; i > 0; i--) {
							if(debug) {
								if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
									System.out.println("Błąd dekodowania. Dane do debugowania: ");
									System.out.println("compressCounter="+compressCounter);
									System.out.println("decompressCounter="+decompressCounter);
									System.out.println("priorDecompressCounter="+priorDecompressCounter);
									System.out.println("current_byte="+(int) current_byte);
									System.out.println("current_word="+current_word);
									System.out.println("current_dword="+current_dword);
									System.out.println("i="+i);
									System.out.println("numerPetli="+numerPetli);
									System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
									for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
										System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
									}
									System.out.println(" |");
									bw.write("current_byte > 0 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
									bw.close();
									//error=true;
									throw new CLZW2Exception("Błąd dekompresji");
								}
							}
							decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
							priorDecompressCounter++;
							decompressCounter++;
						}
						current_byte = compressed_ptr[compressCounter - 2];//↓loc_100EAB82
						current_byte &= 3;
						bw.write("current_byte > 0 -> current_byte >= 64, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (current_byte > 0) {
							//↓loc_100EAB8E
							for (i = current_byte; i > 0; i--) {
								if(debug) {
									if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
										System.out.println("Błąd dekodowania. Dane do debugowania: ");
										System.out.println("compressCounter="+compressCounter);
										System.out.println("decompressCounter="+decompressCounter);
										System.out.println("priorDecompressCounter="+priorDecompressCounter);
										System.out.println("current_byte="+(int) current_byte);
										System.out.println("current_word="+current_word);
										System.out.println("current_dword="+current_dword);
										System.out.println("i="+i);
										System.out.println("numerPetli="+numerPetli);
										System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
										for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
											System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
										}
										System.out.println(" |");
										bw.write("current_byte > 0 -> current_byte >= 64 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
										bw.close();
										//error=true;
										throw new CLZW2Exception("Błąd dekompresji");
									}
								}
								decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
								decompressCounter++;
								compressCounter++;
							}
							current_byte = compressed_ptr[compressCounter];
							compressCounter++;
							bw.write("current_byte > 0 -> current_byte >= 64 -> current_byte > 0, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
					} else if (current_byte >= 32) {//↓loc_100EABD4 (gałąź przed-przed końcem
						//System.out.println("numer pętli: "+numerPetli+"\ncurrent_byte: "+(int) current_byte);
						current_byte &= 31;
						current_dword = current_byte;
						bw.write("current_byte > 0 -> current_byte >= 32, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (current_byte == 0) {
							//current_dword = 0;
							if (compressed_ptr[compressCounter] == 0) {
								current_dword = 0;//↓loc_100EABE3
								do {
									compressCounter++;
									current_byte = compressed_ptr[compressCounter];
									current_dword += 255; //0xFF
								} while (current_byte == 0);
							}
							current_byte = compressed_ptr[compressCounter];
							compressCounter++;//↓loc_100EABF1
							current_dword += current_byte + 31; //0x1F
							bw.write("current_byte > 0 -> current_byte >= 32 -> current_byte == 0, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
						//char[] prior_decompressed_ptr = Arrays.copyOfRange(decompressed_ptr, 0, decompressed_ptr.length); //↓loc_100EABFA
						priorDecompressCounter = decompressCounter;
						current_word = (compressed_ptr[compressCounter]+compressed_ptr[compressCounter+1]*256);
						//System.out.println(String.format("%02X ", (int) compressed_ptr[compressCounter])+String.format("%02X ", (int)compressed_ptr[compressCounter+1]));
						//System.out.println("current_word: "+current_word);
						compressCounter += 2;
						current_word >>= 2;
						//System.out.println("current_word >>= 2: "+current_word);
						//System.out.println("current_dword: "+current_dword+"\n");
						priorDecompressCounter -= current_word;
						priorDecompressCounter--;

						;//↓loc_100EAC59
						i = (int) current_dword;
                        /* wycięte:
                         * loc_100EAC7C
                         * loc_100EAB82
                         * loc_100EAB8E
                         * loc_100EAB9C (jmp)
                        }*/
						
						/*if(numerPetli==11) {
							System.out.println("Debug");
						}*/
						
						/*to co wycięte*/
						if (current_dword >= 6) {
							int ptr_difference = decompressCounter - priorDecompressCounter;
                            i += 2;
							if (ptr_difference >= 4) {
								current_dword += 2;
                                //i += 2;
								for (; i >= 4; i--) {//↓loc_100EAC7C
									if(debug) {
										if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
											System.out.println("Błąd dekodowania. Dane do debugowania: ");
											System.out.println("compressCounter="+compressCounter);
											System.out.println("decompressCounter="+decompressCounter);
											System.out.println("priorDecompressCounter="+priorDecompressCounter);
											System.out.println("current_byte="+(int) current_byte);
											System.out.println("current_word="+current_word);
											System.out.println("current_dword="+current_dword);
											System.out.println("i="+i);
											System.out.println("numerPetli="+numerPetli);
											System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
											for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
												System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
											}
											System.out.println(" |");
											bw.write("current_byte > 0 -> current_byte >= 32 (current_dword >= 6) (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
											bw.close();
											//error=true;
											throw new CLZW2Exception("Błąd dekompresji");
										}
									}
                                    decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
									decompressCounter++;
									priorDecompressCounter++;
								}
							}
                            bw.write("current_byte > 0 -> current_byte >= 32 (current_dword >= 6), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
						else {
							i += 2;

							bw.write("current_byte > 0 -> current_byte >= 32 (current_dword < 6), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");

							//↓loc_100EABC9
							for (; i > 0; i--) {
								if(debug) {
									if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
										System.out.println("Błąd dekodowania. Dane do debugowania: ");
										System.out.println("compressCounter="+compressCounter);
										System.out.println("decompressCounter="+decompressCounter);
										System.out.println("priorDecompressCounter="+priorDecompressCounter);
										System.out.println("current_byte="+(int) current_byte);
										System.out.println("current_word="+current_word);
										System.out.println("current_dword="+current_dword);
										System.out.println("i="+i);
										System.out.println("numerPetli="+numerPetli);
										System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
										for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
											System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
										}
										System.out.println(" |");
										bw.write("current_byte > 0 -> current_byte >= 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
										bw.close();
										//error=true;
										throw new CLZW2Exception("Błąd dekompresji");
									}
								}
								decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
								priorDecompressCounter++;
								decompressCounter++;
							}
						}
						/*if (current_dword >= 6) {
							int ptr_difference = decompressCounter - priorDecompressCounter;
							if (ptr_difference >= 4) {
								//current_dword += 2;
								i += 2;
								for (; i > 0; i--) {//↓loc_100EAC7C
									if(debug) {
										if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
											System.out.println("Błąd dekodowania. Dane do debugowania: ");
											System.out.println("compressCounter="+compressCounter);
											System.out.println("decompressCounter="+decompressCounter);
											System.out.println("priorDecompressCounter="+priorDecompressCounter);
											System.out.println("current_byte="+(int) current_byte);
											System.out.println("current_word="+current_word);
											System.out.println("current_dword="+current_dword);
											System.out.println("i="+i);
											System.out.println("numerPetli="+numerPetli);
											System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
											for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
												System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
											}
											System.out.println(" |");
											//error=true;
											throw new CLZW2Exception("Błąd dekompresji");
										}
									}
                                    decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
									decompressCounter++;
									priorDecompressCounter++;
								}
								current_byte = compressed_ptr[compressCounter-2];//↓loc_100EAB82
								current_byte &= 3;
								//↓loc_100EAB8E
								for ( i = current_byte; i > 0; i--) {
									if(debug) {
										if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
											System.out.println("Błąd dekodowania. Dane do debugowania: ");
											System.out.println("compressCounter="+compressCounter);
											System.out.println("decompressCounter="+decompressCounter);
											System.out.println("priorDecompressCounter="+priorDecompressCounter);
											System.out.println("current_byte="+(int) current_byte);
											System.out.println("current_word="+current_word);
											System.out.println("current_dword="+current_dword);
											System.out.println("i="+i);
											System.out.println("numerPetli="+numerPetli);
											System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
											for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
												System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
											}
											System.out.println(" |");
											//error=true;
											throw new CLZW2Exception("Błąd dekompresji");
										}
									}
									decompressCounter = compressCounter;
									decompressCounter++;
									compressCounter++;
								}
								current_byte = compressed_ptr[compressCounter];
								compressCounter++;
								;//loc_100EAB9C
							}
						}
						//↓loc_100EABBC
						i += 2;*/
						
						//i += (i>13?1:0); //for test
						//if(numerPetli == 2424) i++;
						//if(numerPetli == 2438) i++;

						bw.write("current_byte > 0 -> current_byte >= 32, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");

						//↓loc_100EABC9
						for (; i > 0; i--) {
							if(debug) {
								if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
									System.out.println("Błąd dekodowania. Dane do debugowania: ");
									System.out.println("compressCounter="+compressCounter);
									System.out.println("decompressCounter="+decompressCounter);
									System.out.println("priorDecompressCounter="+priorDecompressCounter);
									System.out.println("current_byte="+(int) current_byte);
									System.out.println("current_word="+current_word);
									System.out.println("current_dword="+current_dword);
									System.out.println("i="+i);
									System.out.println("numerPetli="+numerPetli);
									System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
									for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
										System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
									}
									System.out.println(" |");
									bw.write("current_byte > 0 -> current_byte >= 32 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
									bw.close();
									//error=true;
									throw new CLZW2Exception("Błąd dekompresji");
								}
							}
                            decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
							priorDecompressCounter++;
							decompressCounter++;
						}
						current_byte = compressed_ptr[compressCounter-2];//↓loc_100EAB82
						current_byte &= 3;
						bw.write("current_byte > 0 -> current_byte >= 32, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (current_byte > 0) {
							//↓loc_100EAB8E
							for (i = current_byte; i > 0; i--) {
								if(debug) {
									if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
										System.out.println("Błąd dekodowania. Dane do debugowania: ");
										System.out.println("compressCounter="+compressCounter);
										System.out.println("decompressCounter="+decompressCounter);
										System.out.println("priorDecompressCounter="+priorDecompressCounter);
										System.out.println("current_byte="+(int) current_byte);
										System.out.println("current_word="+current_word);
										System.out.println("current_dword="+current_dword);
										System.out.println("i="+i);
										System.out.println("numerPetli="+numerPetli);
										System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
										for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
											System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
										}
										System.out.println(" |");
										bw.write("current_byte > 0 -> current_byte >= 32 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
										bw.close();
										//error=true;
										throw new CLZW2Exception("Błąd dekompresji");
									}
								}
                                decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
								decompressCounter++;
								compressCounter++;
							}
							current_byte = compressed_ptr[compressCounter];
							compressCounter++;
							bw.write("current_byte > 0 -> current_byte >= 32 -> current_byte > 0, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
					} else if (current_byte >= 16) {//↓loc_100EAC0C (gałąź przed końcem)
						//char[] prior_decompressed_ptr = Arrays.copyOfRange(decompressed_ptr, 0, decompressed_ptr.length);
						priorDecompressCounter = decompressCounter;
						current_dword = current_byte;
						current_dword &= 8;
						current_dword <<= 1;
						priorDecompressCounter -= current_dword;
						current_byte &= 7;
						bw.write("current_byte > 0 -> current_byte >= 16, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (current_byte == 0) {
							if (compressed_ptr[compressCounter] == 0) {
								current_dword = 0;//↓loc_100EAC2B
								do {
									compressCounter++;
									current_byte = compressed_ptr[compressCounter];
									current_dword += 255;
								} while (current_byte == 0);
							}
							compressCounter++;//↓loc_100EAC39
							current_dword += current_byte + 7;
							bw.write("current_byte > 0 -> current_byte >= 16 -> current_byte == 0, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
						short current_signed_word = (short) (compressed_ptr[compressCounter]+compressed_ptr[compressCounter+1]*256);//↓loc_100EAC42
						compressCounter += 2;
						current_signed_word >>= 2;
						priorDecompressCounter -= current_signed_word;
						bw.write("current_byte > 0 -> current_byte >= 16, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (decompressCounter == priorDecompressCounter) {
							bw.write("current_byte > 0 -> current_byte >= 16 (prior_decompressed_ptr == decompressed_ptr /break), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							break;
						} else {
							priorDecompressCounter -= 16384; //0x4000
							;//↓loc_100EAC59
							i = (int) current_dword;
                            /* wycięte:
                             * loc_100EAC7C
                             * loc_100EAB82
                             * loc_100EAB8E
                             * loc_100EAB9C (jmp)
                            }*/

                            /* to co wycięte */
							if (current_dword >= 6) {
								int ptr_difference = decompressCounter - priorDecompressCounter;
                                i += 2;
								if (ptr_difference >= 4) {
									current_dword += 2;
								    //i += 2;
									for (; i >= 4; i--) {//↓loc_100EAC7C
										if(debug) {
											if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
												System.out.println("Błąd dekodowania. Dane do debugowania: ");
												System.out.println("compressCounter="+compressCounter);
												System.out.println("decompressCounter="+decompressCounter);
												System.out.println("priorDecompressCounter="+priorDecompressCounter);
												System.out.println("current_byte="+(int) current_byte);
												System.out.println("current_word="+current_word);
												System.out.println("current_dword="+current_dword);
												System.out.println("i="+i);
												System.out.println("numerPetli="+numerPetli);
												System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
												for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
													System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
												}
												System.out.println(" |");
												bw.write("current_byte > 0 -> current_byte >= 16 (current_dword >= 6) (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
												
												//error=true;
												throw new CLZW2Exception("Błąd dekompresji");
											}
										}
                                        decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
										decompressCounter++;
										priorDecompressCounter++;
									}
								}
                                bw.write("current_byte > 0 -> current_byte >= 16 (current_dword >= 6), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							}
							else {
								i += 2;

								bw.write("current_byte > 0 -> current_byte >= 16 (prior_decompressed_ptr != decompressed_ptr, current_dword < 4), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");

								//↓loc_100EABC9
								for (; i > 0; i--) {
									if(debug) {
										if(decompressed_ptr[priorDecompressCounter] != wzorzec[decompressCounter]){
											System.out.println("Błąd dekodowania. Dane do debugowania: ");
											System.out.println("compressCounter="+compressCounter);
											System.out.println("decompressCounter="+decompressCounter);
											System.out.println("priorDecompressCounter="+priorDecompressCounter);
											System.out.println("current_byte="+(int) current_byte);
											System.out.println("current_word="+current_word);
											System.out.println("current_dword="+current_dword);
											System.out.println("i="+i);
											System.out.println("numerPetli="+numerPetli);
											System.out.println(((priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length)-(priorDecompressCounter-15>0?priorDecompressCounter-15:0))+" znaków z decompressed_ptr:");
											for(int n = (priorDecompressCounter-15>0?priorDecompressCounter-15:0); n<(priorDecompressCounter+15<decompressed_ptr.length?priorDecompressCounter+15:decompressed_ptr.length); n++) {
												System.out.print(" | ("+(int) decompressed_ptr[n]+") "+(n==decompressCounter?"['"+decompressed_ptr[n]+"'] "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+decompressed_ptr[n]+"' "+(decompressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
											}
											System.out.println(" |");
											bw.write("current_byte > 0 -> current_byte >= 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
											bw.close();
											//error=true;
											throw new CLZW2Exception("Błąd dekompresji");
										}
									}
									decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];
									priorDecompressCounter++;
									decompressCounter++;
								}
							}
							current_byte = compressed_ptr[compressCounter - 2];//↓loc_100EAB82
							current_byte &= 3;
							bw.write("current_byte > 0 -> current_byte >= 16 (prior_decompressed_ptr != decompressed_ptr), " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							if (current_byte > 0) {
								//↓loc_100EAB8E
								for (i = current_byte; i > 0; i--) {
									if(debug) {
										if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
											System.out.println("Błąd dekodowania. Dane do debugowania: ");
											System.out.println("compressCounter="+compressCounter);
											System.out.println("decompressCounter="+decompressCounter);
											System.out.println("priorDecompressCounter="+priorDecompressCounter);
											System.out.println("current_byte="+(int) current_byte);
											System.out.println("current_word="+current_word);
											System.out.println("current_dword="+current_dword);
											System.out.println("i="+i);
											System.out.println("numerPetli="+numerPetli);
											System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
											for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
												System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
											}
											System.out.println(" |");
											bw.write("current_byte > 0 -> current_byte >= 16 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
											bw.close();
											//error=true;
											throw new CLZW2Exception("Błąd dekompresji");
										}
									}
									decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
									decompressCounter++;
									compressCounter++;
								}
								current_byte = compressed_ptr[compressCounter];
								compressCounter++;
								bw.write("current_byte > 0 -> current_byte >= 16 (prior_decompressed_ptr != decompressed_ptr) -> current_byte > 0, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
							}
						}
					}
					else {
						current_byte >>= 2;//↓loc_100EACA4
						//char[] prior_decompressed_ptr = Arrays.copyOfRange(decompressed_ptr, 0, decompressed_ptr.length);
						priorDecompressCounter = decompressCounter;
						priorDecompressCounter -= current_byte;
						current_word = decompressed_ptr[priorDecompressCounter];
						current_word <<= 2;
						priorDecompressCounter -= current_word;
						priorDecompressCounter--; //charakterystyczne
						compressCounter++;
                        decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter];//↓loc_100EAB77
						decompressCounter++;
						decompressed_ptr[decompressCounter] = decompressed_ptr[priorDecompressCounter + 1];
						decompressCounter++;

						current_byte = compressed_ptr[compressCounter-2];//↓loc_100EAB82
						current_byte &= 3;
						bw.write("current_byte > 0 -> else, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						if (current_byte > 0) {
							for (/*unsigned*/ i = current_byte; i > 0; i--) {
								if(debug) {
									if(compressed_ptr[compressCounter] != wzorzec[decompressCounter]){
										System.out.println("Błąd dekodowania. Dane do debugowania: ");
										System.out.println("compressCounter="+compressCounter);
										System.out.println("decompressCounter="+decompressCounter);
										System.out.println("priorDecompressCounter="+priorDecompressCounter);
										System.out.println("current_byte="+(int) current_byte);
										System.out.println("current_word="+current_word);
										System.out.println("current_dword="+current_dword);
										System.out.println("i="+i);
										System.out.println("numerPetli="+numerPetli);
										System.out.println(((compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length)-(compressCounter-15>0?compressCounter-15:0))+" znaków z compressed_ptr:");
										for(int n = (compressCounter-15>0?compressCounter-15:0); n<(compressCounter+15<compressed_ptr.length?compressCounter+15:compressed_ptr.length); n++) {
											System.out.print(" | ("+(int) compressed_ptr[n]+") "+(n==compressCounter?"['"+compressed_ptr[n]+"'] "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":""):"'"+compressed_ptr[n]+"' "+(compressed_ptr[n] == wzorzec[decompressCounter]?" (poprawne)":"")));
										}
										System.out.println(" |");
										bw.write("current_byte > 0 -> else -> current_byte > 0 (nieoczekiwany koniec programu), " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
										bw.close();
										//error=true;
										throw new CLZW2Exception("Błąd dekompresji");
									}
								}
                                decompressed_ptr[decompressCounter] = compressed_ptr[compressCounter];
								decompressCounter++;
								compressCounter++;
							}
							current_byte = compressed_ptr[compressCounter];
							compressCounter++;
							bw.write("current_byte > 0 -> else -> current_byte > 0, " + compressCounter + ", " + decompressCounter + ", " + priorDecompressCounter + ", " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
						}
					}
				}
			}
			;//↓loc_100EACBB (gałąź końcowa)
			/*unsigned*/ int ptr_difference = decompressCounter/*decompressed_ptr[decompressCounter] - first_decompressed_ptr*/;
			//reinterpret_cast<unsigned *>(input_ptr)[0] = ptr_difference;
			setSize(0, ptr_difference);
			bw.write("koniec kodu, " + compressCounter + ", " + decompressCounter + ", 0, " + (int) current_byte + ", " + current_word + ", " + current_dword + ", " + i + ", " + numerPetli + "\n");
			bw.close();
			if (compressCounter != last_compressed_ptr) { //błędy - return zwraca co innego
				;//↓loc_100EACCE
				if (compressCounter < last_compressed_ptr) { //wyjście zawiera mniej niż zaplanowano
					//return /*(char *)-8*/ null;
					throw new CLZW2Exception("Wyjście zbyt krótkie ("+compressCounter+" != "+last_compressed_ptr+")");
				} else { //wyjście zawiera więcej niż zaplanowano
					//return /*(char *)-4*/ null;
					throw new CLZW2Exception("Wyjście zbyt długie ("+compressCounter+" != "+last_compressed_ptr+")");
				}
				//oficjalny koniec pętli
			} else {
				return decompressed_ptr;
			}
		} else {
			return null;
		}
	}
}

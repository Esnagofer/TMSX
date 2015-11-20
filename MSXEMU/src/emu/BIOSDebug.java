package emu;

public class BIOSDebug {

	
	
	public static String getDesc(short addr) {
	
		switch (addr) {
		case 0x02D7: return "CHKRAM";
		case 0x1BBF: return "CGTABL";
		case 0x2683: return "SYNCHR";
		case 0x01B6: return "RDSLT";
		case 0x2686: return "CHRGTR";
		case 0x01D1: return "WRSLT";
		case 0x1B45: return "OUTDO";
		case 0x0217: return "CALSLT";
		case 0x146A: return "DCOMPR";
		case 0x025E: return "ENASLT";
		case 0x2689: return "GETYPR";
		case 0x0205: return "CALLF";
		case 0x0C3C: return "KEYINT";
		case 0x049D: return "INITIO";
		case 0x139D: return "INIFNK";
		
		case 0x0577: return "DISSCR";
		case 0x0570: return "ENASCR";
		case 0x057F: return "WRTVDP";
		case 0x07D7: return "RDVRM";
		case 0x07CD: return "WRTVRM";
		case 0x07EC: return "SETRD";
		case 0x07DF: return "SETWRT";
		case 0x0815: return "FILVRM";
		case 0x070F: return "LDIRMV";
		case 0x0744: return "LDIRVM";
		case 0x084F: return "CHGMOD";
		case 0x07F7: return "CHGCLR";

		case 0x1398: return "NMI";

		case 0x06A8: return "CLRSPR";
		case 0x05E4: return "INITXT";
		case 0x0538: return "INIT32";
		case 0x05D2: return "INIGRP";
		case 0x061F: return "INIMLT";
		case 0x0594: return "SETTXT";
		case 0x05B4: return "SETT32";
		case 0x0602: return "SETGRP";
		case 0x0659: return "SETMLT";
		case 0x06E4: return "CALPAT";
		case 0x06F9: return "CALATR";
		case 0x0704: return "GSPSIZ";
		case 0x1510: return "GRPPRT";

		case 0x04BD: return "GICINI";
		case 0x1102: return "WRTPSG";
		case 0x110E: return "RDPSG";
		case 0x11C4: return "STRTMS";

		case 0x0D6A: return "CHSNS";
		case 0x10CB: return "CHGET";
		case 0x08BC: return "CHPUT";
		case 0x085D: return "LPTOUT";
		case 0x0884: return "LPTSTT";
		case 0x089D: return "CNVCHR";

		case 0x23BF: return "PINLIN";
		case 0x23D5: return "INLIN";

		case 0x23CC: return "QINLIN";
		case 0x046F: return "BREAKX";
		case 0x03FB: return "ISCNTC";
		case 0x10F9: return "CKCNTC";
		case 0x1113: return "BEEP";
		case 0x0848: return "CLS";
		case 0x088E: return "POSIT";
		case 0x0B26: return "FNKSB";
		case 0x0B15: return "ERAFNK";
		case 0x0B2B: return "DSPFNK";
		case 0x083B: return "TOTEXT";
	
		case 0x11EE: return "GTSTCK";
		case 0x1253: return "GTTRIG";
		case 0x12AC: return "GTPAD";
		case 0x1273: return "GTPDL";

		case 0x1A36: return "TAPION";
		case 0x1ABC: return "TAPIN";
		case 0x19E9: return "TAPIOF";
		case 0x19F1: return "TAPOON";
		case 0x1A19: return "TAPOUT";
		case 0x19DD: return "TAPOFF";
		case 0x1384: return "STMOTR";

		case 0x14EB: return "LFTQ";
		case 0x1492: return "PUTQ";

		case 0x16C5: return "RIGHTC";
		case 0x16EE: return "LEFTC";
		case 0x175D: return "UPC";
		case 0x173C: return "TUPC";
		case 0x172A: return "DOWNC";
		case 0x170A: return "TDOWNC";
		case 0x1599: return "SCALXY";
		case 0x15DF: return "MAPXYC";
		case 0x1639: return "FETCHC";

		case 0x1640: return "STOREC";

		case 0x1676: return "SETATR";
		case 0x1647: return "READC";
		case 0x167E: return "SETC";
		case 0x1809: return "NSETCX";
		case 0x18C7: return "GTASPC";
		case 0x18CF: return "PNTINI";
		case 0x18E4: return "SCANR";
		case 0x197A: return "SCANL";

		case 0x0F3D: return "CHGCAP";
		case 0x0F7A: return "CHGSND";
		case 0x144C: return "RSLREG";
		case 0x144F: return "WSLREG";
		case 0x1449: return "RDVDP";
		case 0x1452: return "SNSMAT";

		case 0x148A: return "PHYDIO";

		case 0x148E: return "FORMAT";
		case 0x145F: return "ISFLIO";
		case 0x1B63: return "OUTDLP";
		case 0x1470: return "GETVCP";
		case 0x1474: return "GETVC2";
		case 0x0468: return "KILBUF";
		case 0x01FF: return "CALBAS";

		case 0x2680: return "INIT!";
		
		case 0x6678: return "Print message at HL";
		
		case 0x411F: return "MAINLOOP";

		case 0x4134: return "Interpreted Mainloop";
		
		case 0x4601: return "Runloop";

		case 0x4640: return "Runloop execution point";

		case 0x7304: return "Shut down printer";

		case 0x7374: return "Mainloop: collect line of text";

		case 0x7C76: return "powerup";

		case 0x7D29: return "Power-up: routine (function keys, text mode, id message, bytes free)";

		case 0x7D5D: return "Power-up: find lowest RAM";

		case 0x7D75: return "Power-up: extension ROM search";

		}
		
		return "";
		
	}
	
}

package sigars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SystemChecker {
	private static Sigar sSigar;
	private static long  sPid;

	public static void main(String[] args) throws SigarException, InterruptedException {
		sSigar = new Sigar();
		sPid   = sSigar.getPid();

		System.out.println("=====================================");
		System.out.println("========= CPU Data Start ============");
		setTransDataForCPU();
		System.out.println("================ End ================\n");

		System.out.println("=====================================");
		System.out.println("========= Ram Data Start ============");
		setTransDataForRam();
		System.out.println("================ End ================\n");

		System.out.println("=====================================");
		System.out.println("========= Disk Data Start ===========");
		setTransDataForDisk();
		System.out.println("================ End ================\n");

		System.out.println("=====================================");
		System.out.println("========= Network Data Start ========");
		setTransDataForNetwork();
		System.out.println("================ End ================\n");

		System.out.println("=====================================");
		System.out.println("========= Process Data Start ========");
		setTransDataForProcess();
		System.out.println("================ End ================\n");
	}
	
	/**
	 * 데이터를 입력 받아서 사용자가 원하는 단위로 변환을 하여 준다 예)
	 * CommonsUtil.getConvertDataUnit("500", "B", "KB");
	 * 
	 * @param data        String -- 변환 데이터
	 * @param currentUnit String -- 현재 타입(B, KB, MB, GB, TB)
	 * @param changeUnit  String -- 변경 할 타입(B, KB, MB, GB, TB)
	 * @return String
	 */
	private static String convertDataUnit(Long longData, String currentUnit, String convertUnit) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2); // 소숫점 2자리까지만 보이게 변환

		try {
			longData = longData == null ? 0 : longData;

			// 현재 단위의 레벨을 정의 한다
			int currentLvl = 0;
			if      ("B" .equals(currentUnit)) { currentLvl = 1; } // Byte : 1
			else if ("KB".equals(currentUnit)) { currentLvl = 2; } // KiloByte : 2
			else if ("MB".equals(currentUnit)) { currentLvl = 3; } // MegaByte : 3
			else if ("GB".equals(currentUnit)) { currentLvl = 4; } // MegaByte : 4
			else if ("TB".equals(currentUnit)) { currentLvl = 5; } // TeraByte : 5

			// 변경 할 단위의 레벨을 정의 한다
			int convertLvl = 0;
			if      ("B" .equals(convertUnit)) { convertLvl = 1; } // Byte : 1
			else if ("KB".equals(convertUnit)) { convertLvl = 2; } // KiloByte : 2
			else if ("MB".equals(convertUnit)) { convertLvl = 3; } // MegaByte : 3
			else if ("GB".equals(convertUnit)) { convertLvl = 4; } // MegaByte : 4
			else if ("TB".equals(convertUnit)) { convertLvl = 5; } // TeraByte : 5

			// 작은 단위를 큰 단위로 변경 - 레벨에 맞게 나눈다
			if (currentLvl < convertLvl) {
				if      (currentLvl - convertLvl == -1) return nf.format(longData / Math.pow(1024, 1)) + convertUnit;
    			else if (currentLvl - convertLvl == -2) return nf.format(longData / Math.pow(1024, 2)) + convertUnit;
    			else if (currentLvl - convertLvl == -3) return nf.format(longData / Math.pow(1024, 3)) + convertUnit;
    			else if (currentLvl - convertLvl == -4) return nf.format(longData / Math.pow(1024, 4)) + convertUnit;
    			else                                    return longData + convertUnit;
    			
			}
			// 작은 단위를 큰 단위로 변경 - 레벨에 맞게 곱한다
			else if (currentLvl > convertLvl) {
				if      (currentLvl - convertLvl == 1) return nf.format(longData * Math.pow(1024, 1)) + convertUnit;
				else if (currentLvl - convertLvl == 2) return nf.format(longData * Math.pow(1024, 2)) + convertUnit;
				else if (currentLvl - convertLvl == 3) return nf.format(longData / Math.pow(1024, 3)) + convertUnit;
				else if (currentLvl - convertLvl == 4) return nf.format(longData * Math.pow(1024, 4)) + convertUnit;
				else                                   return "1" + convertUnit;
			}
			// 정의 되지 않은 레벨
			else return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	private static void setTransDataForCPU() throws InterruptedException, SigarException {
		CpuInfo[] cpuInfos = sSigar.getCpuInfoList();
		CpuInfo   cpuInfo  = cpuInfos[0];
		CpuPerc   cpuPerc  = sSigar.getCpuPerc();

		System.out.println(cpuInfo.getVendor() + " " + cpuInfo.getModel() + "  " + cpuInfo.getMhz() + "Mhz");
		System.out.println(cpuPerc.toString());
		System.out.println(
				  "wait   : " + CpuPerc.format(cpuPerc.getWait()) + "\n"
				+ "use    : " + CpuPerc.format(cpuPerc.getUser()  +
												cpuPerc.getSys()) + "\n"
				+ "idle   : " + CpuPerc.format(cpuPerc.getIdle())
		);

//		CpuData.getCpuData();
	}

	@SuppressWarnings("unchecked")
	private static void setTransDataForRam() throws SigarException, InterruptedException {
		Mem     mem     = sSigar.getMem();
		ProcMem procMem = sSigar.getProcMem(sPid);
		HashMap<String, Object> procMemMap = (HashMap<String, Object>) procMem.toMap();
		System.out.println(procMemMap);

		HashMap<String, Object> memMap = (HashMap<String, Object>) mem.toMap();
		System.out.println(memMap);
		System.out.println(
				  "Total : " + convertDataUnit(Long.valueOf(memMap.get("Total")+""), "B", "GB")+"\n"
				+ "Free  : " + convertDataUnit(Long.valueOf(memMap.get("Free") +""), "B", "GB")+"(" + (int) (double) Double.valueOf(memMap.get("FreePercent") +"") + "%)\n"
				+ "Used  : " + convertDataUnit(Long.valueOf(memMap.get("Used") +""), "B", "GB")+"(" + (int) (double) Double.valueOf(memMap.get("UsedPercent") +"") + "%)"
		);

//		RamData.getRamData();
	}

	private static void setTransDataForDisk() throws SigarException {
		FileSystemUsage fileSystemUsage = null;

		List<HashMap<String, Object>> nodeDiskInfoList    = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> subNodeDiskInfoList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> nodeDiskInfo    = null;
		HashMap<String, Object> subNodeDiskInfo = null;

		FileSystem[] fileSystemList = sSigar.getFileSystemList();
		for (FileSystem fileSystem : fileSystemList) {
			nodeDiskInfo = new HashMap<String, Object>();
			nodeDiskInfo.put("deviceName"               , fileSystem.getDevName());
//			nodeDiskInfo.put("dirName"                  , fileSystem.getDirName());
//			nodeDiskInfo.put("fileSystemEnvironmentType", fileSystem.getTypeName());
			nodeDiskInfo.put("fileSystemType"           , fileSystem.getSysTypeName());
//			nodeDiskInfo.put("partitionFlags"           , fileSystem.getFlags());
//			nodeDiskInfo.put("options"                  , fileSystem.getOptions());

			try {
				fileSystemUsage = new FileSystemUsage();
				fileSystemUsage.gather(sSigar, fileSystem.getDirName());

				subNodeDiskInfo = new HashMap<String, Object>();
				subNodeDiskInfo.put("totalMemory"    , convertDataUnit(fileSystemUsage.getTotal(), "KB", "GB"));
				subNodeDiskInfo.put("usedMemory"     , convertDataUnit(fileSystemUsage.getUsed() , "KB", "GB"));
				subNodeDiskInfo.put("freeMemory"     , convertDataUnit(fileSystemUsage.getFree() , "KB", "GB"));
				subNodeDiskInfo.put("availableMemory", convertDataUnit(fileSystemUsage.getAvail(), "KB", "GB"));
				subNodeDiskInfo.put("readBytes"      , fileSystemUsage.getDiskReadBytes());
				subNodeDiskInfo.put("reads"          , fileSystemUsage.getDiskReads());
				subNodeDiskInfo.put("writeBytes"     , fileSystemUsage.getDiskWriteBytes());
				subNodeDiskInfo.put("writes"         , fileSystemUsage.getDiskWrites());
				subNodeDiskInfoList.add(subNodeDiskInfo);

				subNodeDiskInfo = null;
			} catch (SigarException e) {}
			nodeDiskInfo.put("subNodeDiskInfoList", subNodeDiskInfoList);
			nodeDiskInfoList.add(nodeDiskInfo);

			nodeDiskInfo = null;
		}

		System.out.println(nodeDiskInfoList.toString());
//		System.out.println(new Gson().nodeDiskInfoList);
	}

	private static void setTransDataForNetwork() throws SigarException, InterruptedException {
		NetworkData.getNetworkData();
	}

	private static void setTransDataForProcess() throws SigarException {
		ProcessData.getProcessData();
	}
}
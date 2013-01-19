package source;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/*
 * vogoalAPI 1.0
 * Auther SinNeR@blueidea.com
 * by vogoal.com
 * mail: vogoals@hotmail.com
 */
/**
 * JSP�ϴ��ļ���
 * 
 * @author SinNeR
 * @version 1.0
 */
public class JspFileUpload {
	/** request���� */
	private HttpServletRequest request = null;
	/** �ϴ��ļ���·�� */
	private String uploadPath = null;
	/** ÿ�ζ�ȡ���ֽڵĴ�С */
	private static int BUFSIZE = 1024 * 8;
	/** �洢������Hashtable */
	private Hashtable<String, ArrayList<String>> paramHt = new Hashtable<String, ArrayList<String>>();
	/** �洢�ϴ����ļ����ļ�����ArrayList */
	private ArrayList<String> updFileArr = new ArrayList<String>();

	/**
	 * �趨request����
	 * 
	 * @param request
	 *            HttpServletRequest request����
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * �趨�ļ��ϴ�·����
	 * 
	 * @param path
	 *            �û�ָ�����ļ����ϴ�·����
	 */
	public void setUploadPath(String path) {
		this.uploadPath = path;
	}

	/**
	 * �ļ��ϴ�����������
	 * 
	 * @return int ������� 0 �ļ������ɹ���1 request���󲻴��ڡ� 2 û���趨�ļ�����·�������ļ�����·������ȷ��3
	 *         û���趨��ȷ��enctype��4 �ļ������쳣��
	 */
	@SuppressWarnings("unchecked")
	public int process() {
		int status = 0;
		// �ļ��ϴ�ǰ����request�����ϴ�·���Լ�enctype����check��
		status = preCheck();
		// �����ʱ�򷵻ش�����롣
		if (status != 0)
			return status;
		try {
			// ���������ļ���u
			String name = null;
			// ������value
			String value = null;
			// ��ȡ�����Ƿ�Ϊ�ļ��ı�־λ
			boolean fileFlag = false;
			// Ҫ�洢���ļ���
			File tmpFile = null;
			// �ϴ����ļ�������
			String fName = null;
			FileOutputStream baos = null;
			BufferedOutputStream bos = null;
			// �洢������Hashtable
			paramHt = new Hashtable<String, ArrayList<String>>();
			updFileArr = new ArrayList<String>();
			int rtnPos = 0;
			byte[] buffs = new byte[BUFSIZE * 8];
			// ȡ��ContentType
			String contentType = request.getContentType();
			int index = contentType.indexOf("boundary=");
			String boundary = "--" + contentType.substring(index + 9);
			String endBoundary = boundary + "--";
			// ��request������ȡ������
			ServletInputStream sis = request.getInputStream();
			// ��ȡ1��
			while ((rtnPos = sis.readLine(buffs, 0, buffs.length)) != -1) {
				String strBuff = new String(buffs, 0, rtnPos,"utf-8");
				// ��ȡ1������n
				if (strBuff.startsWith(boundary)) {
					if (name != null && name.trim().length() > 0) {
						if (fileFlag) {
							bos.flush();
							baos.close();
							bos.close();
							baos = null;
							bos = null;
							updFileArr.add(fName);
						} else {
							Object obj = paramHt.get(name);
							ArrayList<String> al = new ArrayList<String>();
							if (obj != null) {
								al = (ArrayList<String>) obj;
							}
							al.add(value);
				//			System.out.println(name+"::"+value);
							paramHt.put(name, al);
						}
					}
					name = new String();
					value = new String();
					fileFlag = false;
					fName = new String();
					rtnPos = sis.readLine(buffs, 0, buffs.length);
					if (rtnPos != -1) {
						strBuff = new String(buffs, 0, rtnPos,"utf-8");
						if (strBuff.toLowerCase().startsWith(
								"content-disposition: form-data; ")) {
							int nIndex = strBuff.toLowerCase().indexOf(
									"name=\"");
							int nLastIndex = strBuff.toLowerCase().indexOf(
									"\"", nIndex + 6);
							name = strBuff.substring(nIndex + 6, nLastIndex);
						}
						int fIndex = strBuff.toLowerCase().indexOf(
								"filename=\"");
						if (fIndex != -1) {
							fileFlag = true;
							int fLastIndex = strBuff.toLowerCase().indexOf(
									"\"", fIndex + 10);
							fName = strBuff.substring(fIndex + 10, fLastIndex);
							fName = getFileName(fName);
							if (fName == null || fName.trim().length() == 0) {
								fileFlag = false;
								sis.readLine(buffs, 0, buffs.length);
								sis.readLine(buffs, 0, buffs.length);
								sis.readLine(buffs, 0, buffs.length);
								continue;
							} else {
								fName = getFileNameByTime(fName);
								sis.readLine(buffs, 0, buffs.length);
								sis.readLine(buffs, 0, buffs.length);
							}
						}
					}
				} else if (strBuff.startsWith(endBoundary)) {
					if (name != null && name.trim().length() > 0) {
						if (fileFlag) {
							bos.flush();
							baos.close();
							bos.close();
							baos = null;
							bos = null;
							updFileArr.add(fName);
						} else {
							Object obj = paramHt.get(name);
							ArrayList<String> al = new ArrayList<String>();
							if (obj != null) {
								al = (ArrayList<String>) obj;
							}
							al.add(value);
							paramHt.put(name, al);
						}
					}
				} else {
					if (fileFlag) {
						if (baos == null && bos == null) {
							tmpFile = new File(uploadPath + fName);
							baos = new FileOutputStream(tmpFile);
							bos = new BufferedOutputStream(baos);
						}
						bos.write(buffs, 0, rtnPos);
						baos.flush();
					} else {
//						System.out.println("test :" + value + "--" + strBuff);
						value = value + strBuff;
					}
				}
			}
		} catch (IOException e) {
			status = 4;
		}
		return status;
	}

	private int preCheck() {
		int errCode = 0;
		if (request == null)
			return 1;
		if (uploadPath == null || uploadPath.trim().length() == 0)
			return 2;
		else {
			File tmpF = new File(uploadPath);
			if (!tmpF.exists())
				return 2;
		}
		String contentType = request.getContentType();
		if (contentType.indexOf("multipart/form-data") == -1)
			return 3;
		return errCode;
	}

	public String getParameter(String name) {
		String value = "";
		if (name == null || name.trim().length() == 0)
			return value;
		value = (paramHt.get(name) == null) ? ""
				: (String) ((ArrayList<String>) paramHt.get(name)).get(0);
		return value;
	}

	public String[] getParameters(String name) {
		if (name == null || name.trim().length() == 0)
			return null;
		if (paramHt.get(name) == null)
			return null;
		ArrayList<String> al = (ArrayList<String>) paramHt.get(name);
		String[] strArr = new String[al.size()];
		for (int i = 0; i < al.size(); i++)
			strArr[i] = (String) al.get(i);
		return strArr;
	}

	public int getUpdFileSize() {
		return updFileArr.size();
	}

	public String[] getUpdFileNames() {
		String[] strArr = new String[updFileArr.size()];
		for (int i = 0; i < updFileArr.size(); i++)
			strArr[i] = (String) updFileArr.get(i);
		return strArr;
	}

	private String getFileName(String input) {
		int fIndex = input.lastIndexOf("\\");
		if (fIndex == -1) {
			fIndex = input.lastIndexOf("/");
			if (fIndex == -1) {
				return input;
			}
		}
		input = input.substring(fIndex + 1);
		return input;
	}

	private String getFileNameByTime(String input) {
		int index = input.lastIndexOf(".");
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return input.substring(0, index) + sdf.format(dt)
				+ input.substring(index);
	}
}
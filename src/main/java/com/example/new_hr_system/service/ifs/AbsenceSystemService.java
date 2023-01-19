package com.example.new_hr_system.service.ifs;

import javax.servlet.http.HttpSession;

import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;
import com.example.new_hr_system.vo.EmployeeInfoRes;

public interface AbsenceSystemService {

	// ���u�s�W����(�浧)
	// ��J:���s,���O,���(Date), email ��X:����(uuid,���s,���O,���(Date)), ���\�T��, email
	// �\��:���u�s�W����,���U�s�W���Ыذ���ñH�eemail�q���D�ާ尲
	public AbsenceSystemRes addAbsence(AbsenceSystemReq req, HttpSession httpSession);

	// ���u�s�W����(�h��)
	// ��J:���s,���O,���(Date), email ��X:����(uuid,���s,���O,���(Date)), ���\�T��, email
	// �\��:���u�s�W����,���U�s�W���Ыذ���ñH�eemail�q���D�ާ尲
	public AbsenceSystemRes addAbsences(AbsenceSystemReq req, HttpSession httpSession);

	// �R������
	// ��J:uuid ��X:���\�T��
	// �\��:�̷ӫe�ݶǦ^�Ӫ�uuid�R������
	public AbsenceSystemRes deleteAbsence(AbsenceSystemReq req);

	// ���u��ܦۤv������
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession);

	public AbsenceSystemResList getAbsenceByEmployeeCodeAndDate(AbsenceSystemReq req, HttpSession httpSession);

	// �D����ܦP�������u������
	// ��J:�D�ޭ��s ��X:�а����u�����s,�m�W,����,���O,���(Date)
	// �\��:�̷ӫe�ݶǦ^���D�ޭ��s,��ܻP�ӥD�ެۦP�����B���ťH�U�����u����
	public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession);

	// �D�ާ�㰲��
	// ��J:uuid,1��2,�а���� ��X:�s�W���ӥX��(uuid, ���s, �X�Ԥ�(DateTime), �X�����p(�а�), �X�Ԯɼ�(0)), ���\�T��,
	// email
	// �\��:�P�_�e�ݧ�㰲�檺����p�G�O��㰲��(1)�h�s�W���ӥX�ԻP�H�eemail�������u, �Y�����(2)�h�u�H�eemail
	public AbsenceSystemRes checkYesOrNo(AbsenceSystemReq req);

	// �P�_���u����(�p��2�����I)
	public boolean checkEmployeeLevel(HttpSession httpSession);

	public AbsenceSystemRes updateAbsence(AbsenceSystemReq req);

	// ���u��ܦۤv�����D�ު�email
	public EmployeeInfoRes getManagerEmailByEmployeeCode(HttpSession httpSession);

}

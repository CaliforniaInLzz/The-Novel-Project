package novel;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Scanner;



public class Client implements Serializable{

	private static final long serialVersionUID = -6958651296303907666L;
	private static Socket socket;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	public static void main(String[] args) throws UnknownHostException, IOException, SQLException, ClassNotFoundException, InterruptedException 
	{
		socket = new Socket("127.0.0.1",1560);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());	
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());	
		try {
			while(true)							//����������
			{
				menu1();							//��ʾ������	
				int cs1=choose1AndCheck();			//������ѡ�� ���� 
				if(cs1==10)continue;					//�����˷�1 2 ����ʾ���󲢷���������
				else if(cs1==1)						//ѡ���½
				{
					boolean isLogIn = logIn();		//ִ�е�½ģ��ɹ�����true ʧ�ܷ���false ʧ����ʾ���ص�������
					while(isLogIn)					//��¼�ɹ�ִ��while�������	
					{
						munu2();						//��½�ɹ���ѡ�����
						int cs2 = choose2AndCheck();	//�������ѡ��	cs3ֻ�����ĸ�ֵ 0 1 2 --3���������012
						if(cs2==0)break;				//ѡ��0 (�ص����˵�)����ѭ�����ص�������
						else if(cs2==10)continue;		//�����˷�1 2 ����ѭ��
						else if(cs2==1)					//ѡ��1 �������Ķ���ִ�����´���
						{
							while(true)
							{
								munu3();					//�����Ķ�ѡ�����
								int cs3 = choose3AndCheck();//�����Ķ�ѡ�����
								if(cs3==6)continue;			//�õ�6���� ������� ����ѡ��
								else if(cs3==0)break;		//����0 ���� �����ϼ��˵� ����½�ɹ����ѡ��
								else	//ѡ���			
								{		
									if(!munu4(cs3))continue;	//�������false ˵������ص��ϼ��˵�
								}
							}
						}
						else if(cs2==2)							//ѡ��2С˵����
						{
							while(true)
							{
								munu3();					//�����Ķ�ѡ�����
								int cs3 = choose3AndCheck();//�����Ķ�ѡ�����
								if(cs3==6)continue;			//�õ�6���� ������� ����ѡ��
								else if(cs3==0)break;		//����0 ���� �����ϼ��˵� ����½�ɹ����ѡ��
								else						//ѡ���			
								{	
									if(!downLoadChooise(cs3))continue;	//�������false ˵������ص��ϼ��˵�
								}
							}			
						}
						else if(cs2==3)				//ѡ��С˵�ϴ�
						{
							while(true)
							{
								uploadView();		//С˵��ͼ
								int cs_6=upload();
								if(cs_6==10)continue;		//����10 ˵���������,Ҫ��������
								if(cs_6==0)break;			//����0���뷵���ϼ��˵�
								if(cs_6==1)
								{
									while(true)
									{
										int cs_7=upload2();			//�����ϴ�������
										if(cs_7==10)continue;		//10˵���������
										if(cs_7==0)break;			//0˵�������һ���˵�
										int cs_8 =upload3(cs_7);				//ѡ�����ϴ�����
										if(cs_8==0)
										{
											continue;				//����0�������ѡ��С˵����
										}
										if(cs_8==520)
										{
											break;
										}
									}
									
								}
							}
						}
						// ��½�������ڴ���չ���� +else if 	
					}		
				}
				else if	(cs1==2)					//ע��
				{
					if(enrol())						//ע��ɹ�
					continue;
				}
				else 					//�˳�ģ��
				{
					break;
				}	
			}
		} catch (Exception e) {
			System.out.println("�ͻ��˳����˴���,��رձȿͻ��˴������ӷ����� ,������Client_122");
		}finally{
			System.out.println("****************c u*********************");
			try {
				ois.close();
			} catch (Exception e2) {
			}finally{
				try {
					oos.close();
				} catch (IOException e) {
			
				}finally{
					try {
						dis.close();
					} catch (IOException e) {
						
					}finally{
						try {
							dos.close();
						} catch (IOException e) {
						}finally{
							socket.close();
						}
					}
				}
			}
		}		
	}

	
	/*
	 * С˵�ϴ���ͼ
	 * 
	 */
	
	static  void uploadView()
	{
		System.out.println("---------------------------------");
		System.out.println("1�� С˵�ϴ�");
		System.out.println("2�� �鿴����");
		System.out.println("0�� �˳�ϵͳ");
		System.out.println("---------------------------------");
		System.out.println("����������ѡ�� ��");
	}
	
	//С˵�ϴ���������߼��ƶ�
	static int upload() throws InterruptedException
	{

		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"0".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//����1���� 0
		}
		if("2".equals(choose1))
		{
			System.out.println("**************����*****************");
			System.out.println("**�����ϴ�ɫ��,�����ȵ���С˵*********");
			System.out.println("**һ�н���Ȩ�� @������ ����***********");
			System.out.println("**********************************");
			System.out.println("5���ҳ���Զ���ת......");
			Thread.sleep(4999);
			return 10;	
		}
		System.out.println("������0 1 2 ");
		return 10;
	}
	static int upload2()
	{
		System.out.println("**********С˵�ϴ�ϵͳ*************");
		System.out.println("1�� ����");
		System.out.println("2�� ����");
		System.out.println("3: ����");
		System.out.println("4: ����");
		System.out.println("5: У԰");
		System.out.println("0�� �����ϼ��˵�");
		System.out.println("��������Ҫ�ϴ���С˵����");
		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"0".equals(choose1)||"2".equals(choose1)||"3".equals(choose1)||"4".equals(choose1)||"5".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//����1-5
		}
		System.out.println("��������ȷ��ѡ�� ");
		return 10;
	}
	static int upload3(int cs7) throws InterruptedException, IOException
	{
		while(true)
		{
			String type;	
			if(cs7==1)
				type="����";
			else if(cs7==2)
				type="����";
			else if(cs7==3)
				type="����";
			else if(cs7==4)
				type="����";
			else
				type="У԰";
			System.out.println("***********��ѡ����: "+type+"����*****************");
			System.out.println("�������ѡ��С˵���������·�����0");
			System.out.println("������С˵�����ֻ�0,���ܳ���10λ");
		
			String novelName;
			while(true)				//�������볤��
			{
				Scanner scanner = new Scanner(System.in);
				novelName = scanner.next();
				if(novelName.length()>=10)
				{
					System.out.println("���Ȳ��ܴ���10λ,������������:");	
					continue;
				}
				break;
			}
			if("0".equals(novelName))
			{
				return Integer.parseInt(novelName); 		//����0 �������ѡ��С˵����
			}
			while(true)
			{
				System.out.println("*************����С˵��Ϊ: "+novelName+"***************");
				System.out.println("����С˵���������·�����0");
				System.out.println("������С˵�����߻�0,������С��6λ:");
				Scanner scanner2 = new Scanner(System.in);
				String author = scanner2.next();
				if("0".equals(author))
				{
					break;						//����0 �����������С˵����
				}
				if(author.length()>=6)
				{
					System.out.println("��������̫��,����������");
					continue;
				}
				System.out.println("*************����С˵����Ϊ: "+author+"***************");
				System.out.println("���������������������·�����0");
				System.out.println("������С˵�ļ��:");
				Scanner scanner3 = new Scanner(System.in);
				String synopsis = scanner3.next();
				
				if("0".equals(synopsis))
				{
					continue;						//����0 ���������������
				}
				if(synopsis.length()>=30)
				{
					System.out.println("С˵������30λ,���������");
					continue;						//����0 ���������������
				}
				System.out.println("****************��Ϣ����**************************");
				System.out.println("***����С˵��Ϊ: "+novelName);
				System.out.println("***����С˵����Ϊ: "+author);
				System.out.println("***����С˵����Ϊ: "+type);
				System.out.println("***����С˵���Ϊ: "+synopsis);
				System.out.println("************************************");
		
				while(true)
				{
					System.out.println("��������С˵��·��,ȡ���ϴ�������0");
					Scanner scanner6 = new Scanner(System.in);
					String novelPath = scanner6.next();
					if(novelPath.length()>40)
					{
						System.out.println("��С˵·�����ȴ���40,������������");
						continue;
					}
					File file0 = new File(novelPath);
					if("0".equals(novelPath))
					{
						return 520;					//���»ص�С˵����ѡ��		
					}
					if(!file0.exists())		//����õ���·������ ��ô����null
					{
						System.out.println("��·��������,���������");continue;
					}
					if(!file0.isFile())		//����õ���·������ ��ô����null
					{
						System.out.println("��·����Ŀ¼�����ļ�,���������");continue;
					}
					
					if(!file0.canRead())		//����õ���·������ ��ô����null
					{
						System.out.println("���ļ����ɶ�,���������");continue;
					}
					System.out.println("�ļ�·�����óɹ�.........");
					RandomAccessFile upLoadNovel = new RandomAccessFile(file0,"rw");
					byte [] upLoadNovelContent = new byte[(int) upLoadNovel.length()];
					upLoadNovel.read(upLoadNovelContent);		//�ļ�����

					upLoadNovel.close();			//������Ϊʲô????����close?
					System.out.println("*************�Ƿ�ȷ���ύ?***********");
					System.out.println("1:�ύ  0:ȡ��");
					Scanner scanner4 = new Scanner(System.in);
					String last = scanner4.next();
					if("0".equals(last))
					{
						System.out.println("********************");
						System.out.println("���ź�,�ڴ������´��ϴ�");
						System.out.println("5���Ϊ����ת���ϴ�ҳ��");
						Thread.sleep(5000);
						return 520;						//����520�˳��ϴ�	
					}
					if("1".equals(last))
					{
						System.out.println("************************");
						System.out.println("����С˵�����ϴ�,���Ժ�...");
						dos.writeInt(Op.UPLOAD);
						dos.writeInt(upLoadNovelContent.length);		//�ѳ��ȴ���ȥ
						dos.writeUTF(type);								//С˵����
						dos.writeUTF(novelName);						//С˵��
						dos.writeUTF(author);							//������
						dos.writeUTF(synopsis);							//С˵�����
						dos.flush();
						dos.writeInt(Op.UPLOAD2);
						dos.write(upLoadNovelContent,0,upLoadNovelContent.length);
						dos.flush();
						break;
					}
				}
				
				boolean idSucceed = dis.readBoolean();						//�鿴�ϴ��Ƿ�ɹ�
				if(idSucceed)
				{
					System.out.println("��ϲ��,С˵�ϴ��ɹ�!");
					System.out.println("5���Ϊ����ת��ҳ��");
					Thread.sleep(5000);
					return 520;
				}
				System.out.println("���ź�,С˵�ϴ�ʧ����,����ϵ����");
				System.out.println("5���Ϊ����ת��ҳ��");
				Thread.sleep(5000);
				return 520;

			}
			continue;	
		}
	
	}
	/*
	 * ���С˵�б� ��������
	 * 
	 * 
	 */
	public static boolean downLoadChooise(int cs3) throws IOException, ClassNotFoundException, InterruptedException
	{	
		while(true)
		{
			String type;	
			if(cs3==1)
				type="����";
			else if(cs3==2)
				type="����";
			else if(cs3==3)
				type="����";
			else if(cs3==4)
				type="����";
			else
				type="У԰";
			dos.writeInt(Op.NOVEL_TYPE);
			dos.writeInt(cs3);		//С˵����1-5�����������
			dos.flush();
			String[] bookmessage =  (String[]) ois.readObject();//�õ��鱾��Ϣ��װ��������
			System.out.println("-----------------------");
			System.out.println("\t����\t����\t���");
			
			
			int i=0;												//i��Ϊ�˿��������ж���������
			for(String messages:bookmessage)						//foreach	ȡ����Ϣ
			{
				i++;
				String[] message= messages.split(Op.encrypt+"");	//	����		
				System.out.println(" "+i+":\t"+message[0]+"\t"+message[1]+"\t"+message[2]);
																//�����һ��������
			}
			System.out.println(" 0:\t������һ���˵�");
			System.out.println("-----------�б����-----------");
			System.out.println("����������Ҫ���ص�С˵���:");
			Scanner cs4 = new Scanner(System.in);
			String Choose4 = cs4.next();
			if(!CheckIsNumber(Choose4))			
			{
				System.out.println("��������ȷ��ѡ�� �������벻������");continue;		//���������һ��������
			}
			int cs_4 = Integer.parseInt(Choose4);					//�������� ����
			if(cs_4==0) return false;								//����0 ����false
			if(0<Integer.parseInt(Choose4)&&i>=Integer.parseInt(Choose4))		//����������С˵��ѡ�� ����������1-i
			{
				dos.writeInt(Op.LOOK);
				dos.writeInt(cs_4);									//ѡ���ǵڼ���С˵����ȥ
				dos.writeUTF(type);									//��С˵�����ʹ���ȥ
				dos.flush();
				int novelLength = (int)dis.readLong();					//�õ����鳤��
				if(novelLength==-1)
				{
					System.out.println("��Ǹ.��С˵������·������,������Ա���ᾡ���޸���");
					System.out.println("5���ص�С˵�б�........");
					Thread.sleep(5000);
					continue;
				}
				byte [] novelContent = new byte[novelLength];			//�õ�С˵����		
				dis.readFully(novelContent);
				if(!downloadNovel(cs_4,type,novelContent))continue;		//�𷵻��ϼ��˵�������
				//��ת����ģ��
			}
			System.out.println("��������ȷ��ѡ�� ����������ֲ��ڷ�Χ��");continue;			//������һ��������Χ������
		}
	}
	
	/*
	 * ����ģ��
	 */
	
	static boolean downloadNovel(int cs_4,String type,byte [] novelContent) throws IOException, InterruptedException
	{
		while(true)
		{
			System.out.println("����������Ҫ����С˵�����õ�Ŀ¼,����0����С˵�б�");
			Scanner scanner = new Scanner(System.in);
			String path = scanner.next();
			if("0".equals(path))
			{
				return false;
			}
			String path2 = path+java.io.File.separator+type+cs_4+".txt";
			RandomAccessFile randomAccess;
			try
			{
				randomAccess = new RandomAccessFile(path2,"rw");	
			}catch (Exception e) {
//				e.printStackTrace();
				System.out.println("С˵·������");continue;
			}			
			
			File file = new File(path2);
			if(!file.exists())			//���洴���˻������� ��˵������ʧ��
			{
				System.out.println("С˵·������,����ʧ�� ���������С˵·��");continue;
			}
			randomAccess.write(novelContent);
			System.out.println("С˵���سɹ�,����ȥ"+path+"Ŀ¼�²鿴:"+type+cs_4+".txt");
			System.out.println("5����Զ��ص�С˵�б�.......");
			Thread.sleep(4999);
			return false;
		}
	}

	//��ҳ����ͼ
	static void menu1()								
	{
		System.out.println("---------------------------------");
		System.out.println("1�� ��¼");
		System.out.println("2�� ע��");
		System.out.println("0�� �˳�");
		System.out.println("---------------------------------");
		System.out.println("����������ѡ�� ��");	
	}
	
	/*	������ѡ�����ģ�� 
	 * ����1����1	 ����2����2
	 * ������������ʾ������������
	 */
	static int choose1AndCheck()
	{
		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"2".equals(choose1)||"0".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//����1����2
		}
		System.out.println("������1 2 0");
		return 10;										//����1����2	0
	}
	
	/*
	 * ��½�ɹ����������
	 */
	
	public static void munu2()				
	{
		System.out.println("---------------------------------");
		System.out.println("1�� �����Ķ�");
		System.out.println("2�� С˵����");
		System.out.println("3�� С˵�ϴ�");
		System.out.println("0�� �ص����˵�");
		System.out.println("---------------------------------");
		System.out.println("����������ѡ�� ��");
	}
	/*
	 * ��½�ɹ���ѡ�����ģ�� 
	 * ����3����û��������ȷ��ѡ��
	 */
	
	static int choose2AndCheck()			//��½�ɹ���ѡ����� ������0,1,2,3 10
	{
		Scanner scanner = new Scanner(System.in);
		String choose2 = scanner.next();
		if("1".equals(choose2)||"2".equals(choose2)||"0".equals(choose2)||"3".equals(choose2))
		{
			return Integer.parseInt(choose2); 		//����1����2
		}
		System.out.println("��������ȷ��ѡ��");
		return 10;											//����1����2 3	 �򷵻�10
	}

	/*
	 * 
	 * �����Ķ�������
	 * 
	 */	
	public static void munu3()
	{	
		System.out.println("---------------------------------");
		System.out.println("1�� ����");
		System.out.println("2�� ����");
		System.out.println("3: ����");
		System.out.println("4: ����");
		System.out.println("5: У԰");
		System.out.println("0�� �����ϼ��˵�");
		System.out.println("---------------------------------");
		System.out.println("����������ѡ�� ��");
	}
	/*
	 * �����Ķ�ѡ�������
	 */
	static int choose3AndCheck()			//����ѡ��
	{
		Scanner scanner = new Scanner(System.in);
		String choose3 = scanner.next();
		if("1".equals(choose3)||"2".equals(choose3)||"3".equals(choose3)||
				"4".equals(choose3)||"5".equals(choose3)||"0".equals(choose3))
		{
			return Integer.parseInt(choose3); 		//����1����2
		}
		System.out.println("��������ȷ��ѡ��");
		return 6;											//����1����2	
	}
	
	
	
	/*
	 * ѡ��С˵���ͺ�Ľ���
	 * ����false ��������
	 */
	
	public static boolean munu4(int cs3) throws IOException, ClassNotFoundException, InterruptedException
	{	
		while(true)
		{
			String type;	
			if(cs3==1)
				type="����";
			else if(cs3==2)
				type="����";
			else if(cs3==3)
				type="����";
			else if(cs3==4)
				type="����";
			else
				type="У԰";
			dos.writeInt(Op.NOVEL_TYPE);
			dos.writeInt(cs3);		//С˵����1-5�����������
			dos.flush();
			String[] bookmessage =  (String[]) ois.readObject();//�õ��鱾��Ϣ��װ��������
			System.out.println("-----------------------");
			System.out.println("\t����\t����\t���");

			int i=0;												//i��Ϊ�˿��������ж���������
			for(String messages:bookmessage)						//foreach	ȡ����Ϣ
			{
				i++;
				String[] message= messages.split(Op.encrypt+"");	//	����		
				System.out.println(" "+i+":\t"+message[0]+"\t"+message[1]+"\t"+message[2]);
																//�����һ��������
			}
			System.out.println(" 0:\t������һ���˵�");
			System.out.println("------------�б����-------------");
			System.out.println("����������ѡ��:");
			Scanner cs4 = new Scanner(System.in);
			String Choose4 = cs4.next();
			if(!CheckIsNumber(Choose4))			
			{
				System.out.println("��������ȷ��ѡ�� �������벻������");continue;		//���������һ��������
			}
			int cs_4 = Integer.parseInt(Choose4);					//�������� ����
			if(cs_4==0) return false;								//����0 ����false
			if(0<Integer.parseInt(Choose4)&&i>=Integer.parseInt(Choose4))		//����������С˵��ѡ�� ����������1-i
			{
				dos.writeInt(Op.LOOK);
				dos.writeInt(cs_4);									//ѡ���ǵڼ���С˵����ȥ
				dos.writeUTF(type);									//��С˵�����ʹ���ȥ
				dos.flush();
				int novelLength = (int)dis.readLong();					//�õ����鳤��
				if(novelLength==-1)
				{
					System.out.println("��Ǹ.��С˵������·������,������һ��С˵,������Ա���ᾡ���޸���");
					System.out.println("5���ص�С˵�б�........");
					Thread.sleep(4999);
					continue;
				}
				byte [] novelContent = new byte[novelLength];			//�õ�С˵����		
				dis.readFully(novelContent);
				if(!pagination(novelLength,novelContent))continue;		//�ѳ��Ⱥ�byte[]���鴫��ȥ���з�ҳ��ʾ
																		//���������false �����뷵���ϼ��˵�
			}
			System.out.println("��������ȷ��ѡ�� ����������ֲ��ڷ�Χ��");continue;			//������һ��������Χ������
		}
	}
	
	/*
	 * ��δ������ڼ��һ���ַ����Ƿ���ת����int����
	 */
	public static Boolean CheckIsNumber(String c)
	{
		try
		{
			Integer.parseInt(c);
			
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * �����Ķ���ʾģ��
	 * �����鳤�Ⱥ��������ݴ����� ���з�ҳ��ʾ
	 */
	public static boolean pagination(int novelLength,byte[] novelContent) throws InterruptedException
	{
			String content = new String(novelContent);
			int page = content.length()/300+1; //һ������ҳ ÿҳ500��
			int endPageByte = content.length()%300;
			if(page-1==0)
			{
				System.out.println("***********************************************************************");
				System.out.println("**********���ź�,����û������,�����ѡ��,5���Ϊ����ת.......");
				System.out.println("***********************************************************************");
				Thread.sleep(4500);
			}
			for(int i=0;i<page-1;i++)
			{
				System.out.println("***********************************************************************");

				String view = content.substring(i*300,i*300+300);//new String(novelContent,i*300,300);
				System.out.println(view);
				if(i==page-2)		//���ǵ����ڶ�ҳ �Ǿ�ȫ���������
				{
					String view2 = content.substring(i*300+300,i*300+300+endPageByte);//new String(novelContent,i*300+300,endPageByte);
					System.out.println(view2);
				}
				Scanner choose5 = new Scanner(System.in);
				System.out.println("***********************************************************************");
				System.out.println("-----------0:�˻��ϼ�����---------------1����һҳ 2:��һҳ---------------");
				while(true)
				{
					System.out.println("��������ѡ�� ��");
					String cs_5 = choose5.next();	
					if(!CheckIsNumber(cs_5))			//��Ϊ���֣���Ϊ��ĸ
					{
						System.out.println("������������");	continue;							//������ִ������
					}
					else if("0".equals(cs_5))
					{
						System.out.println("����Ϊ�������ϼ��˵�.......");return false;
					}
					else if("1".equals(cs_5)&&i==0)
					{
						System.out.println("��ǰ���ǵ�һҳ,����ִ����һҳ����."); continue;
					}
					else if("1".equals(cs_5))	//1�����뿴��һҳ��
					{
						i=i-2; break;
					}
					else if("2".equals(cs_5)&&i==page-2)
					{
						System.out.println("��ǰ�������һҳ,����ִ����һҳ����.�����ϼ��˵�������0"); continue;
					}
					else if("2".equals(cs_5))break;	//2���뿴��һҳ
					else
					{
						System.out.println("��������0,1,2 ��Ҫ������������ ");	continue;
					}
				}
			}
			return false;
	}
	/*	
	 * ��½ģ��	(���)
	 * ����true��ʾ��¼�ɹ�,����false��ʾ��¼ʧ��
	 * ���Թ�������
	 * ������Ч���ѡ���˳�������������
	 */
	static boolean logIn() throws IOException	
	{
		System.out.println("---------------------------------");
		System.out.println("�����������˺ţ�");
		Scanner scanner = new Scanner(System.in); 
		String account = scanner.next().trim();
		while(!(account!=null&&account!=""))
		{
			System.out.println("������������Ч���˺�,���ز˵�������110");
			account = scanner.next().trim();
			if("110".equals(account))
			{
				return false;
			}
		}
		System.out.println("�������������룺");
		String password = scanner.next().trim();
		while(!(password!=null&&password!=""))
		{
			System.out.println("������������Ч������,���ز˵�������120");
			password = scanner.next().trim();
			if("120".equals(account))
			{
				return false;
			}
		}
		dos.writeInt(Op.accounAndPasswordIn);
		dos.writeUTF(account+","+password);
		dos.flush();
		if(dis.readBoolean())
		{
			System.out.println("�˺�������ȷ����¼�ɹ�");
			return true;
		}
		else
		{
			System.out.println("�˺�������󣬵�¼ʧ��");return false;
		}
	}

	/*
	 * ע��ģ��(���)
	 */
	static boolean enrol() throws IOException						//ע��
	{
		System.out.println("----------��ӭע��--------------");		//ΪʲôҪ������?��ӭһ�ξ͹���
		while(true)
		{
			System.out.println("�����������˺�,���ȱ���С��10λ");
			Scanner scanner = new Scanner(System.in);
			String account = scanner.next();					//�����˺�
			if(account.length()>=10)
			{
				System.out.println("********************************");
				System.out.println("��������˺ų��ڻ����10λ,���������");
				continue;
			}
			dos.writeInt(Op.accounIn);
			dos.writeUTF(account);
			dos.flush();
			if(dis.readBoolean())
			{
				System.out.println("���ʺ��ѱ�ע����������ע��");
				continue;
			}
			String password="";
			while(true)
			{
				System.out.println("��������������,����С��15λ");
				password = scanner.next();				//��������
				if(password.length()>=15)
				{
					System.out.println("********************************");
					System.out.println("����������볤�ڻ��ߵ���15λ,���������");
					continue;
				}
				break;
			}
			System.out.println("����Ϊ��ע��,���Ե�Ƭ��...");
			dos.writeInt(Op.LOGIN);
			dos.writeUTF(account);
			dos.writeUTF(password);
			dos.flush();
			if(dis.readBoolean())
			{
				System.out.println("��ϲ��,ע��ɹ���");
				return true;
			}
			System.out.println("�ǳ��ź�,ע��ʧ����");
		}	
	}
}
//*********************************************This is my fist project*********************************************************************
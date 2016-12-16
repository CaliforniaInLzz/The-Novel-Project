package novel;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Serializable {

	private static final long serialVersionUID = -9141236735349617462L;
	public static void main(String[] args) throws IOException, SQLException 
	{
		ServerSocket serversocket = new ServerSocket(1560,2);
		ExecutorService pool=Executors.newCachedThreadPool();
		try {
			
			System.out.println("****************************************");
			System.out.println("**************�����������ɹ�**************");
			System.out.println("***��ǰʱ��: "+new Date());
			System.out.println("****************************************");
			while (true) 
			{
				
				Socket socket = serversocket.accept();
				ServerTask st=new ServerTask(socket);
				pool.submit(st);
				
			}
		} 
		finally
		{
			serversocket.close();
		}
	}
	
	
	public static class ServerTask implements Callable<Void> {
		private Socket socket;

		public ServerTask(Socket socket) {
			this.socket = socket;
		}

		@SuppressWarnings("finally")
		@Override
		public Void call(){
			System.out.println("InetAddress:\t"+socket.getInetAddress()+"\t������������");
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;;
			DataOutputStream dos = null;
			DataInputStream dis = null;
			FileInputStream fis=null;
			FileOutputStream fos=null;
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
				int novelLength=0;
				String type2 ="";
				String novelName = "";						
				String author = "";						
				String synopsis = "";
				while(true)
				{
					switch(dis.readInt())												//��ȡѡ��
					{
					case Op.accounAndPasswordIn:										//��֤��¼��Ϣ ��½�ɹ�����true
						String accountAndPasswordIn = dis.readUTF();
						dos.writeBoolean(check_account_password(accountAndPasswordIn));
						dos.flush();break;
					case Op.accounIn:
						String accountIn = dis.readUTF();								//��֤�Ƿ�����˻� ���ڷ���true
						dos.writeBoolean(check_account(accountIn));
						dos.flush();break;
					case Op.LOGIN:
						String newClientAccount=dis.readUTF();
						String newClientPassword=dis.readUTF();
						boolean isLogin = login(newClientAccount,newClientPassword);
						dos.writeBoolean(isLogin);
						dos.flush();
						if(isLogin)
							System.out.println("���û���InetAddress:"+socket.getInetAddress());
						break;	//ע��ɹ�����true
					case Op.NOVEL_TYPE:											//	
						int type_choose = dis.readInt();
						oos.writeObject(nover_classify_by_type(type_choose));	//��ͻ��˴��������С˵��Ϣ
						oos.flush();											//
						break;
					case Op.LOOK:			//�ͷ��˷����������Ķ�С˵����
						int cs_4 = dis.readInt();								//��ȡ�ڼ�
						String type = dis.readUTF();							//��ȡ����
						if(getContent(cs_4,type)==null)							//�����null ����·������
						{
							dos.writeLong(-1);
							dos.flush();
							break;
						}														
						//·��û��ʹ����ȥ
						dos.writeLong(getContent(cs_4,type).length);			//�����鳤�ȴ���ȥ
						dos.write(getContent(cs_4,type));						//��byte���鴫��ȥ
						dos.flush();break;										//С˵������byte������ʽ���͹�ȥ
					case Op.UPLOAD:				//�Ȱ�С˵�ⲿ������
						novelLength = dis.readInt();
						type2 = dis.readUTF();							//��ȡС˵����
						novelName = dis.readUTF();						//С˵��
						author = dis.readUTF();							//С˵����
						synopsis = dis.readUTF();						//С˵���
						break;
					case Op.UPLOAD2:				//����дһ����Ϊ���ϴ������ܶ�һЩ Ŀǰֻ���ϴ�23K 2����ְ�
						byte[] novelContent = new byte[novelLength];			//�½��������ڽ���С˵����
						dis.read(novelContent);	
						boolean result = upLoad(type2,novelName,author,synopsis,novelContent);	//����Ϣ������������
						if(result)
						{
							System.out.println("���û���InetAddress:"+socket.getInetAddress());
						}
						dos.writeBoolean(result);		//����true�����ϴ��ɹ���
						dos.flush();
						novelLength=0;
						type2 ="";
						novelName = "";						
						author = "";						
						synopsis = "";
						break;
					} 
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}finally{
				try {
					ois.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}finally{
					try {
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}finally{
						try {
							dis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}finally{
							try {
								dos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
							}finally{
								try {
									System.out.println("InetAddress:\t"+socket.getInetAddress()+"\t�뿪������");
									socket.close();
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();
								}finally{
									
									return null;
								}
							}
						}
					}
				}
			}		
		}
	}
	//С˵�ϴ�������� ���ɹ��ر�
	static boolean upLoad(String type2,String novelName,String author,String synopsis,byte[] novelContent)
	{
		
		Connection con=null;
		PreparedStatement ps = null;
		try {
			String sql="insert into novel values(?,?,?,?,?)";
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );	
			String path = "D:\\lzm\\novel\\novel";
			String path2 = path+java.io.File.separator+"�û��ϴ�С˵"+novelName+"-"+author+(int)Math.random()*20+".txt";	//��������������·��
			ps.setString(1,author);				//ΪʲôҪ��random�� Ϊ�˷�ֹ�û�֮���ϴ�ͬ��ͬ����С˵������һ�û���С˵������	
			ps.setString(2,synopsis);
			ps.setString(3,type2);
			ps.setString(4,novelName);
			ps.setString(5,path2);
			int a = ps.executeUpdate();
			if(a==1)
			{
				RandomAccessFile file = new RandomAccessFile(path2,"rw");
				file.write(novelContent);
				file.close();				//�ϴ���ȥ��
			}
			System.out.println("С˵ "+novelName+"-"+author+" �ϴ���"+"\t"+new Date());
			return true;
		} 
		catch (Exception e) 
		{	
			e.printStackTrace();
			return false;	//���쳣�����ϴ�ʧ��
		}
		finally
		{
			try {
				ps.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					con.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}
			}
		}
	}
	//�������֤�˺������Ƿ���ȷ
	static boolean check_account_password(String accountAndPasswordIn){
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select account,password from client";
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			rs = ps.executeQuery();
			rs.beforeFirst();
			while(rs.next())
			{
				String account = rs.getString("account");
				String account_Password = account+","+rs.getString("password");
				if(account_Password.equals(accountAndPasswordIn))
				{
					System.out.println("�û�:"+account+"\t"+"�ɹ���½,time:"+new Date());	return true;
				}	
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		finally
		{
			try {
				rs.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					ps.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}finally
				{
					try {
						con.close();
					} catch (Exception e4) {
						e4.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * ����Ƿ���ڴ��û����Ĵ����
	 * ���ڷ���true
	 * �����ڷ���false
	 */
	static boolean check_account(String accountIn) throws IOException, SQLException{
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select account from client";
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			rs = ps.executeQuery();
			rs.beforeFirst();
			while(rs.next())
			{
				String account = rs.getString("account");
				if(account.equals(accountIn))
				{
					return true;
				}	
			}
			return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally
		{
			try {
				rs.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					ps.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}finally
				{
					try {
						con.close();
					} catch (Exception e4) {
						e4.printStackTrace();
					}
				}
			}
		}
	}
	
	/*
	 * ע������
	 * ע��ɹ�����true
	 * ʧ�ܷ���false
	 */
	static boolean login(String newClientAccount,String newClientPassword) throws IOException, SQLException{

		Connection con=null;
		PreparedStatement ps=null;
		try {
			String sql2="insert into client (account,password) values (?,?)";	
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			ps.setString(1,newClientAccount);
			ps.setString(2,newClientPassword);
			int i = ps.executeUpdate();
			if(i==1)
			{
				System.out.println("�û�:"+newClientAccount+"\t"+"�ɹ�ע��,time:"+new Date());
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
		finally
		{
			try {
				ps.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					con.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}
			}
		}	
	}
	/*
	 * ����С˵��� 
	 * ���� ������͵�rs����
	 */
	static String[] nover_classify_by_type(int type) throws SQLException
	{
		String typeName;
		if(type==1)
			typeName="����";
		else if(type==2)
			typeName="����";
		else if(type==3)
			typeName="����";		
		else if(type==4)
			typeName="����";
		else
			typeName="У԰";	
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select bookname,author,synopsis from novel where type=?";	//�õ������͵�С˵��,����,���
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			ps.setString(1,typeName);
			rs = ps.executeQuery();
			rs.beforeFirst();
			int i=0;
			while(rs.next())										//��ֻ��Ϊ��֪��rs�ж�� i��rs�ĳ���
			{
				i++;
			}
			rs.beforeFirst();
			
			String[] bookmessage=new String[i];
			int j=0;
			while(rs.next())										//�����м�¼��һ����������
			{
				bookmessage[j] = rs.getString("bookname")+Op.encrypt
				+rs.getString("author")+Op.encrypt+
				rs.getString("synopsis")+Op.encrypt+typeName;
				j++;
			}
			return bookmessage;
		} catch (Exception e) {
			return new String[0];// TODO: handle exception
		}
		finally
		{
			try {
				rs.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					ps.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}finally
				{
					try {
						con.close();
					} catch (Exception e4) {
						e4.printStackTrace();
					}
				}
			}
		}	
	}
	//�õ�ָ������ָ������������
	static byte[] getContent(int cs_4,String type) throws SQLException, IOException
	{
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select contentPath from novel where type=?";	//�õ������͵�С˵��,����,���
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			ps.setString(1, type);
			rs = ps.executeQuery();
			rs.beforeFirst();
			for(int i=0;i<cs_4;i++)
			{
				rs.next();
			}
			String Path = rs.getString(1);	//�õ�·��
			if(Path==null)				//�����null ����null
				return null;
			File file0 = new File(Path);
			if(!file0.exists()||!file0.canRead())		//����õ���·������ ��ô����null
			{
				return null;
			}
			RandomAccessFile file = new RandomAccessFile(Path, "r");
			byte [] novelContent = new byte[(int) file.length()];
			file.read(novelContent);								//�����ݶ���byte������
			file.close();											//������ᱨ���˵� �Ѿ�ȷ���ж��ҿɶ���
			return novelContent;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;		
		}
		finally
		{
			try {
				rs.close();
			} catch (Exception e2) {				//�رո�������
				e2.printStackTrace();
			}finally{
				try
				{
					ps.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}finally
				{
					try {
						con.close();
					} catch (Exception e4) {
						e4.printStackTrace();
					}
				}
			}
		}	
	}
	
}

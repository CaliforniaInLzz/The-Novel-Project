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
			System.out.println("**************服务器启动成功**************");
			System.out.println("***当前时间: "+new Date());
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
			System.out.println("InetAddress:\t"+socket.getInetAddress()+"\t连接至服务器");
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
					switch(dis.readInt())												//读取选项
					{
					case Op.accounAndPasswordIn:										//验证登录信息 登陆成功返回true
						String accountAndPasswordIn = dis.readUTF();
						dos.writeBoolean(check_account_password(accountAndPasswordIn));
						dos.flush();break;
					case Op.accounIn:
						String accountIn = dis.readUTF();								//验证是否存在账户 存在返回true
						dos.writeBoolean(check_account(accountIn));
						dos.flush();break;
					case Op.LOGIN:
						String newClientAccount=dis.readUTF();
						String newClientPassword=dis.readUTF();
						boolean isLogin = login(newClientAccount,newClientPassword);
						dos.writeBoolean(isLogin);
						dos.flush();
						if(isLogin)
							System.out.println("该用户的InetAddress:"+socket.getInetAddress());
						break;	//注册成功返回true
					case Op.NOVEL_TYPE:											//	
						int type_choose = dis.readInt();
						oos.writeObject(nover_classify_by_type(type_choose));	//向客户端传输所需的小说信息
						oos.flush();											//
						break;
					case Op.LOOK:			//客服端发来的在线阅读小说请求
						int cs_4 = dis.readInt();								//读取第几
						String type = dis.readUTF();							//读取类型
						if(getContent(cs_4,type)==null)							//如果是null 代表路径错了
						{
							dos.writeLong(-1);
							dos.flush();
							break;
						}														
						//路径没错就传输过去
						dos.writeLong(getContent(cs_4,type).length);			//把数组长度传过去
						dos.write(getContent(cs_4,type));						//把byte数组传过去
						dos.flush();break;										//小说内容以byte数组形式发送过去
					case Op.UPLOAD:				//先把小说外部传过来
						novelLength = dis.readInt();
						type2 = dis.readUTF();							//读取小说类型
						novelName = dis.readUTF();						//小说名
						author = dis.readUTF();							//小说种类
						synopsis = dis.readUTF();						//小说简介
						break;
					case Op.UPLOAD2:				//单独写一个是为了上传内容能多一些 目前只能上传23K 2万多字把
						byte[] novelContent = new byte[novelLength];			//新建数组用于接收小说内容
						dis.read(novelContent);	
						boolean result = upLoad(type2,novelName,author,synopsis,novelContent);	//把信息传给函数处理
						if(result)
						{
							System.out.println("该用户的InetAddress:"+socket.getInetAddress());
						}
						dos.writeBoolean(result);		//返回true表明上传成功了
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
									System.out.println("InetAddress:\t"+socket.getInetAddress()+"\t离开服务器");
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
	//小说上传功能完成 流成功关闭
	static boolean upLoad(String type2,String novelName,String author,String synopsis,byte[] novelContent)
	{
		
		Connection con=null;
		PreparedStatement ps = null;
		try {
			String sql="insert into novel values(?,?,?,?,?)";
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );	
			String path = "D:\\lzm\\novel\\novel";
			String path2 = path+java.io.File.separator+"用户上传小说"+novelName+"-"+author+(int)Math.random()*20+".txt";	//保存至服务器的路径
			ps.setString(1,author);				//为什么要用random呢 为了防止用户之间上传同名同类型小说导致另一用户的小说被覆盖	
			ps.setString(2,synopsis);
			ps.setString(3,type2);
			ps.setString(4,novelName);
			ps.setString(5,path2);
			int a = ps.executeUpdate();
			if(a==1)
			{
				RandomAccessFile file = new RandomAccessFile(path2,"rw");
				file.write(novelContent);
				file.close();				//上传回去了
			}
			System.out.println("小说 "+novelName+"-"+author+" 上传于"+"\t"+new Date());
			return true;
		} 
		catch (Exception e) 
		{	
			e.printStackTrace();
			return false;	//出异常表明上传失败
		}
		finally
		{
			try {
				ps.close();
			} catch (Exception e2) {				//关闭各种链接
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
	//服务端验证账号密码是否正确
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
					System.out.println("用户:"+account+"\t"+"成功登陆,time:"+new Date());	return true;
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
			} catch (Exception e2) {				//关闭各种链接
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
	 * 检测是否存在此用户名的代码块
	 * 存在返回true
	 * 不存在返回false
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
			} catch (Exception e2) {				//关闭各种链接
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
	 * 注册代码块
	 * 注册成功返回true
	 * 失败返回false
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
				System.out.println("用户:"+newClientAccount+"\t"+"成功注册,time:"+new Date());
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
			} catch (Exception e2) {				//关闭各种链接
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
	 * 输入小说编号 
	 * 返回 这个类型的rs内容
	 */
	static String[] nover_classify_by_type(int type) throws SQLException
	{
		String typeName;
		if(type==1)
			typeName="武侠";
		else if(type==2)
			typeName="言情";
		else if(type==3)
			typeName="都市";		
		else if(type==4)
			typeName="玄幻";
		else
			typeName="校园";	
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select bookname,author,synopsis from novel where type=?";	//得到该类型的小说名,作者,简介
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			ps.setString(1,typeName);
			rs = ps.executeQuery();
			rs.beforeFirst();
			int i=0;
			while(rs.next())										//我只是为了知道rs有多大 i是rs的长度
			{
				i++;
			}
			rs.beforeFirst();
			
			String[] bookmessage=new String[i];
			int j=0;
			while(rs.next())										//把所有记录放一个数组里面
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
			} catch (Exception e2) {				//关闭各种链接
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
	//得到指定类型指定书名的内容
	static byte[] getContent(int cs_4,String type) throws SQLException, IOException
	{
		Connection con=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql2="select contentPath from novel where type=?";	//得到该类型的小说名,作者,简介
			con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1428;DatabaseName=mini_novel","LZM","460950464");
			ps=con.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
			ps.setString(1, type);
			rs = ps.executeQuery();
			rs.beforeFirst();
			for(int i=0;i<cs_4;i++)
			{
				rs.next();
			}
			String Path = rs.getString(1);	//得到路径
			if(Path==null)				//如果是null 返回null
				return null;
			File file0 = new File(Path);
			if(!file0.exists()||!file0.canRead())		//如果得到的路径不对 那么返回null
			{
				return null;
			}
			RandomAccessFile file = new RandomAccessFile(Path, "r");
			byte [] novelContent = new byte[(int) file.length()];
			file.read(novelContent);								//把内容读进byte数组里
			file.close();											//这个不会报错了的 已经确认有而且可读了
			return novelContent;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;		
		}
		finally
		{
			try {
				rs.close();
			} catch (Exception e2) {				//关闭各种链接
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

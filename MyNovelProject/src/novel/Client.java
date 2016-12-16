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
			while(true)							//进入主界面
			{
				menu1();							//显示主界面	
				int cs1=choose1AndCheck();			//主界面选择 过滤 
				if(cs1==10)continue;					//输入了非1 2 会提示错误并返回主界面
				else if(cs1==1)						//选择登陆
				{
					boolean isLogIn = logIn();		//执行登陆模块成功返回true 失败返回false 失败提示并回到主界面
					while(isLogIn)					//登录成功执行while里面代码	
					{
						munu2();						//登陆成功后选择界面
						int cs2 = choose2AndCheck();	//检验过滤选择	cs3只会有四个值 0 1 2 --3代表输入非012
						if(cs2==0)break;				//选择0 (回到主菜单)跳出循环，回到主界面
						else if(cs2==10)continue;		//输入了非1 2 从新循环
						else if(cs2==1)					//选择1 （在线阅读）执行以下代码
						{
							while(true)
							{
								munu3();					//在线阅读选择界面
								int cs3 = choose3AndCheck();//在线阅读选择过滤
								if(cs3==6)continue;			//得到6代表 输入错误 从新选择
								else if(cs3==0)break;		//输入0 代表 返回上级菜单 即登陆成功后的选择
								else	//选择后			
								{		
									if(!munu4(cs3))continue;	//如果返回false 说明他想回到上级菜单
								}
							}
						}
						else if(cs2==2)							//选择2小说下载
						{
							while(true)
							{
								munu3();					//在线阅读选择界面
								int cs3 = choose3AndCheck();//在线阅读选择过滤
								if(cs3==6)continue;			//得到6代表 输入错误 从新选择
								else if(cs3==0)break;		//输入0 代表 返回上级菜单 即登陆成功后的选择
								else						//选择后			
								{	
									if(!downLoadChooise(cs3))continue;	//如果返回false 说明他想回到上级菜单
								}
							}			
						}
						else if(cs2==3)				//选择小说上传
						{
							while(true)
							{
								uploadView();		//小说视图
								int cs_6=upload();
								if(cs_6==10)continue;		//返回10 说明输入错误,要重新输入
								if(cs_6==0)break;			//发挥0是想返回上级菜单
								if(cs_6==1)
								{
									while(true)
									{
										int cs_7=upload2();			//进入上传界面了
										if(cs_7==10)continue;		//10说明输入错误
										if(cs_7==0)break;			//0说明想回上一级菜单
										int cs_8 =upload3(cs_7);				//选择了上传类型
										if(cs_8==0)
										{
											continue;				//返回0代表从新选择小说类型
										}
										if(cs_8==520)
										{
											break;
										}
									}
									
								}
							}
						}
						// 登陆后界面可在此扩展功能 +else if 	
					}		
				}
				else if	(cs1==2)					//注册
				{
					if(enrol())						//注册成功
					continue;
				}
				else 					//退出模块
				{
					break;
				}	
			}
		} catch (Exception e) {
			System.out.println("客户端出现了错误,请关闭比客户端从新连接服务器 ,错误编号Client_122");
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
	 * 小说上传视图
	 * 
	 */
	
	static  void uploadView()
	{
		System.out.println("---------------------------------");
		System.out.println("1： 小说上传");
		System.out.println("2： 查看声明");
		System.out.println("0： 退出系统");
		System.out.println("---------------------------------");
		System.out.println("请输入您的选择 ：");
	}
	
	//小说上传的输入后逻辑推断
	static int upload() throws InterruptedException
	{

		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"0".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//返回1或者 0
		}
		if("2".equals(choose1))
		{
			System.out.println("**************声明*****************");
			System.out.println("**请勿上传色情,暴力等低俗小说*********");
			System.out.println("**一切解释权归 @蓝子明 所有***********");
			System.out.println("**********************************");
			System.out.println("5秒后本页面自动跳转......");
			Thread.sleep(4999);
			return 10;	
		}
		System.out.println("请输入0 1 2 ");
		return 10;
	}
	static int upload2()
	{
		System.out.println("**********小说上传系统*************");
		System.out.println("1： 武侠");
		System.out.println("2： 言情");
		System.out.println("3: 都市");
		System.out.println("4: 玄幻");
		System.out.println("5: 校园");
		System.out.println("0： 返回上级菜单");
		System.out.println("请输入想要上传的小说类型");
		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"0".equals(choose1)||"2".equals(choose1)||"3".equals(choose1)||"4".equals(choose1)||"5".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//返回1-5
		}
		System.out.println("请输入正确的选择 ");
		return 10;
	}
	static int upload3(int cs7) throws InterruptedException, IOException
	{
		while(true)
		{
			String type;	
			if(cs7==1)
				type="武侠";
			else if(cs7==2)
				type="言情";
			else if(cs7==3)
				type="都市";
			else if(cs7==4)
				type="玄幻";
			else
				type="校园";
			System.out.println("***********您选择了: "+type+"类型*****************");
			System.out.println("若想从新选择小说类型请在下方输入0");
			System.out.println("请输入小说的名字或0,不能长于10位");
		
			String novelName;
			while(true)				//控制输入长度
			{
				Scanner scanner = new Scanner(System.in);
				novelName = scanner.next();
				if(novelName.length()>=10)
				{
					System.out.println("长度不能大于10位,请您重新输入:");	
					continue;
				}
				break;
			}
			if("0".equals(novelName))
			{
				return Integer.parseInt(novelName); 		//返回0 代表从新选择小说类型
			}
			while(true)
			{
				System.out.println("*************您的小说名为: "+novelName+"***************");
				System.out.println("返回小说命名请在下方输入0");
				System.out.println("请输入小说的作者或0,作者名小于6位:");
				Scanner scanner2 = new Scanner(System.in);
				String author = scanner2.next();
				if("0".equals(author))
				{
					break;						//返回0 代表从新设置小说名字
				}
				if(author.length()>=6)
				{
					System.out.println("作者名字太长,请重新输入");
					continue;
				}
				System.out.println("*************您的小说作者为: "+author+"***************");
				System.out.println("如果想从新设置作者请在下方输入0");
				System.out.println("请输入小说的简介:");
				Scanner scanner3 = new Scanner(System.in);
				String synopsis = scanner3.next();
				
				if("0".equals(synopsis))
				{
					continue;						//返回0 代表从新设置作者
				}
				if(synopsis.length()>=30)
				{
					System.out.println("小说简介大于30位,请从新设置");
					continue;						//返回0 代表从新设置作者
				}
				System.out.println("****************信息总览**************************");
				System.out.println("***您的小说名为: "+novelName);
				System.out.println("***您的小说作者为: "+author);
				System.out.println("***您的小说类型为: "+type);
				System.out.println("***您的小说简介为: "+synopsis);
				System.out.println("************************************");
		
				while(true)
				{
					System.out.println("请输入您小说的路径,取消上传请输入0");
					Scanner scanner6 = new Scanner(System.in);
					String novelPath = scanner6.next();
					if(novelPath.length()>40)
					{
						System.out.println("您小说路径长度大于40,请您从新输入");
						continue;
					}
					File file0 = new File(novelPath);
					if("0".equals(novelPath))
					{
						return 520;					//从新回到小说类型选择		
					}
					if(!file0.exists())		//如果得到的路径不对 那么返回null
					{
						System.out.println("该路径不存在,请从新输入");continue;
					}
					if(!file0.isFile())		//如果得到的路径不对 那么返回null
					{
						System.out.println("该路径是目录不是文件,请从新输入");continue;
					}
					
					if(!file0.canRead())		//如果得到的路径不对 那么返回null
					{
						System.out.println("该文件不可读,请从新输入");continue;
					}
					System.out.println("文件路径设置成功.........");
					RandomAccessFile upLoadNovel = new RandomAccessFile(file0,"rw");
					byte [] upLoadNovelContent = new byte[(int) upLoadNovel.length()];
					upLoadNovel.read(upLoadNovelContent);		//文件长度

					upLoadNovel.close();			//不明白为什么????不能close?
					System.out.println("*************是否确认提交?***********");
					System.out.println("1:提交  0:取消");
					Scanner scanner4 = new Scanner(System.in);
					String last = scanner4.next();
					if("0".equals(last))
					{
						System.out.println("********************");
						System.out.println("很遗憾,期待您的下次上传");
						System.out.println("5秒后为您跳转回上传页面");
						Thread.sleep(5000);
						return 520;						//返回520退出上传	
					}
					if("1".equals(last))
					{
						System.out.println("************************");
						System.out.println("您的小说正在上传,请稍候...");
						dos.writeInt(Op.UPLOAD);
						dos.writeInt(upLoadNovelContent.length);		//把长度穿过去
						dos.writeUTF(type);								//小说类型
						dos.writeUTF(novelName);						//小说名
						dos.writeUTF(author);							//作者名
						dos.writeUTF(synopsis);							//小说简介绍
						dos.flush();
						dos.writeInt(Op.UPLOAD2);
						dos.write(upLoadNovelContent,0,upLoadNovelContent.length);
						dos.flush();
						break;
					}
				}
				
				boolean idSucceed = dis.readBoolean();						//查看上传是否成功
				if(idSucceed)
				{
					System.out.println("恭喜您,小说上传成功!");
					System.out.println("5秒后为您跳转主页面");
					Thread.sleep(5000);
					return 520;
				}
				System.out.println("很遗憾,小说上传失败了,请联系版主");
				System.out.println("5秒后为您跳转主页面");
				Thread.sleep(5000);
				return 520;

			}
			continue;	
		}
	
	}
	/*
	 * 输出小说列表 给予下载
	 * 
	 * 
	 */
	public static boolean downLoadChooise(int cs3) throws IOException, ClassNotFoundException, InterruptedException
	{	
		while(true)
		{
			String type;	
			if(cs3==1)
				type="武侠";
			else if(cs3==2)
				type="言情";
			else if(cs3==3)
				type="都市";
			else if(cs3==4)
				type="玄幻";
			else
				type="校园";
			dos.writeInt(Op.NOVEL_TYPE);
			dos.writeInt(cs3);		//小说种类1-5传输给服务器
			dos.flush();
			String[] bookmessage =  (String[]) ois.readObject();//得到书本信息封装在数组里
			System.out.println("-----------------------");
			System.out.println("\t书名\t作者\t简介");
			
			
			int i=0;												//i是为了看该类型有多少条数据
			for(String messages:bookmessage)						//foreach	取出信息
			{
				i++;
				String[] message= messages.split(Op.encrypt+"");	//	解密		
				System.out.println(" "+i+":\t"+message[0]+"\t"+message[1]+"\t"+message[2]);
																//输出完一个隔行用
			}
			System.out.println(" 0:\t返回上一级菜单");
			System.out.println("-----------列表结束-----------");
			System.out.println("请输入您想要下载的小说编号:");
			Scanner cs4 = new Scanner(System.in);
			String Choose4 = cs4.next();
			if(!CheckIsNumber(Choose4))			
			{
				System.out.println("请输入正确的选择 您的输入不是数字");continue;		//如果输入了一个非数字
			}
			int cs_4 = Integer.parseInt(Choose4);					//输入数字 分析
			if(cs_4==0) return false;								//输入0 返回false
			if(0<Integer.parseInt(Choose4)&&i>=Integer.parseInt(Choose4))		//这里是输入小说的选择 经过过滤是1-i
			{
				dos.writeInt(Op.LOOK);
				dos.writeInt(cs_4);									//选择是第几号小说穿过去
				dos.writeUTF(type);									//把小说的类型传过去
				dos.flush();
				int novelLength = (int)dis.readLong();					//得到数组长度
				if(novelLength==-1)
				{
					System.out.println("抱歉.此小说服务器路径出错,服务人员将会尽快修复的");
					System.out.println("5秒后回到小说列表........");
					Thread.sleep(5000);
					continue;
				}
				byte [] novelContent = new byte[novelLength];			//得到小说内容		
				dis.readFully(novelContent);
				if(!downloadNovel(cs_4,type,novelContent))continue;		//起返回上级菜单的作用
				//跳转下载模块
			}
			System.out.println("请输入正确的选择 您输入的数字不在范围内");continue;			//输入了一个超过范围的数字
		}
	}
	
	/*
	 * 下载模块
	 */
	
	static boolean downloadNovel(int cs_4,String type,byte [] novelContent) throws IOException, InterruptedException
	{
		while(true)
		{
			System.out.println("请输入您想要下载小说所放置的目录,输入0返回小说列表");
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
				System.out.println("小说路径有误");continue;
			}			
			
			File file = new File(path2);
			if(!file.exists())			//上面创建了还不存在 就说明创建失败
			{
				System.out.println("小说路径有误,创建失败 请从新输入小说路径");continue;
			}
			randomAccess.write(novelContent);
			System.out.println("小说下载成功,请您去"+path+"目录下查看:"+type+cs_4+".txt");
			System.out.println("5秒后自动回到小说列表.......");
			Thread.sleep(4999);
			return false;
		}
	}

	//主页面视图
	static void menu1()								
	{
		System.out.println("---------------------------------");
		System.out.println("1： 登录");
		System.out.println("2： 注册");
		System.out.println("0： 退出");
		System.out.println("---------------------------------");
		System.out.println("请输入您的选择 ：");	
	}
	
	/*	主界面选择过滤模块 
	 * 输入1返回1	 输入2返回2
	 * 输入其他会提示并返回主界面
	 */
	static int choose1AndCheck()
	{
		Scanner scanner = new Scanner(System.in);
		String choose1 = scanner.next();
		if("1".equals(choose1)||"2".equals(choose1)||"0".equals(choose1))
		{
			return Integer.parseInt(choose1); 		//返回1或者2
		}
		System.out.println("请输入1 2 0");
		return 10;										//不是1或者2	0
	}
	
	/*
	 * 登陆成功后操作界面
	 */
	
	public static void munu2()				
	{
		System.out.println("---------------------------------");
		System.out.println("1： 在线阅读");
		System.out.println("2： 小说下载");
		System.out.println("3： 小说上传");
		System.out.println("0： 回到主菜单");
		System.out.println("---------------------------------");
		System.out.println("请输入您的选择 ：");
	}
	/*
	 * 登陆成功后选择过滤模块 
	 * 返回3代表没能输入正确的选项
	 */
	
	static int choose2AndCheck()			//登陆成功后选择过滤 并返回0,1,2,3 10
	{
		Scanner scanner = new Scanner(System.in);
		String choose2 = scanner.next();
		if("1".equals(choose2)||"2".equals(choose2)||"0".equals(choose2)||"3".equals(choose2))
		{
			return Integer.parseInt(choose2); 		//返回1或者2
		}
		System.out.println("请输入正确的选项");
		return 10;											//不是1或者2 3	 则返回10
	}

	/*
	 * 
	 * 在线阅读主界面
	 * 
	 */	
	public static void munu3()
	{	
		System.out.println("---------------------------------");
		System.out.println("1： 武侠");
		System.out.println("2： 言情");
		System.out.println("3: 都市");
		System.out.println("4: 玄幻");
		System.out.println("5: 校园");
		System.out.println("0： 返回上级菜单");
		System.out.println("---------------------------------");
		System.out.println("请输入您的选择 ：");
	}
	/*
	 * 在线阅读选择过滤器
	 */
	static int choose3AndCheck()			//检验选择
	{
		Scanner scanner = new Scanner(System.in);
		String choose3 = scanner.next();
		if("1".equals(choose3)||"2".equals(choose3)||"3".equals(choose3)||
				"4".equals(choose3)||"5".equals(choose3)||"0".equals(choose3))
		{
			return Integer.parseInt(choose3); 		//返回1或者2
		}
		System.out.println("请输入正确的选项");
		return 6;											//不是1或者2	
	}
	
	
	
	/*
	 * 选择小说类型后的界面
	 * 返回false 代表跳出
	 */
	
	public static boolean munu4(int cs3) throws IOException, ClassNotFoundException, InterruptedException
	{	
		while(true)
		{
			String type;	
			if(cs3==1)
				type="武侠";
			else if(cs3==2)
				type="言情";
			else if(cs3==3)
				type="都市";
			else if(cs3==4)
				type="玄幻";
			else
				type="校园";
			dos.writeInt(Op.NOVEL_TYPE);
			dos.writeInt(cs3);		//小说种类1-5传输给服务器
			dos.flush();
			String[] bookmessage =  (String[]) ois.readObject();//得到书本信息封装在数组里
			System.out.println("-----------------------");
			System.out.println("\t书名\t作者\t简介");

			int i=0;												//i是为了看该类型有多少条数据
			for(String messages:bookmessage)						//foreach	取出信息
			{
				i++;
				String[] message= messages.split(Op.encrypt+"");	//	解密		
				System.out.println(" "+i+":\t"+message[0]+"\t"+message[1]+"\t"+message[2]);
																//输出完一个隔行用
			}
			System.out.println(" 0:\t返回上一级菜单");
			System.out.println("------------列表结束-------------");
			System.out.println("请输入您的选择:");
			Scanner cs4 = new Scanner(System.in);
			String Choose4 = cs4.next();
			if(!CheckIsNumber(Choose4))			
			{
				System.out.println("请输入正确的选择 您的输入不是数字");continue;		//如果输入了一个非数字
			}
			int cs_4 = Integer.parseInt(Choose4);					//输入数字 分析
			if(cs_4==0) return false;								//输入0 返回false
			if(0<Integer.parseInt(Choose4)&&i>=Integer.parseInt(Choose4))		//这里是输入小说的选择 经过过滤是1-i
			{
				dos.writeInt(Op.LOOK);
				dos.writeInt(cs_4);									//选择是第几号小说穿过去
				dos.writeUTF(type);									//把小说的类型传过去
				dos.flush();
				int novelLength = (int)dis.readLong();					//得到数组长度
				if(novelLength==-1)
				{
					System.out.println("抱歉.此小说服务器路径出错,请您换一部小说,服务人员将会尽快修复的");
					System.out.println("5秒后回到小说列表........");
					Thread.sleep(4999);
					continue;
				}
				byte [] novelContent = new byte[novelLength];			//得到小说内容		
				dis.readFully(novelContent);
				if(!pagination(novelLength,novelContent))continue;		//把长度和byte[]数组传过去进行分页显示
																		//如果返回是false 则是想返回上级菜单
			}
			System.out.println("请输入正确的选择 您输入的数字不在范围内");continue;			//输入了一个超过范围的数字
		}
	}
	
	/*
	 * 这段代码用于检测一个字符串是否能转换成int类型
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
	 * 在线阅读显示模块
	 * 把数组长度和数组内容传回来 进行分页显示
	 */
	public static boolean pagination(int novelLength,byte[] novelContent) throws InterruptedException
	{
			String content = new String(novelContent);
			int page = content.length()/300+1; //一共多少页 每页500字
			int endPageByte = content.length()%300;
			if(page-1==0)
			{
				System.out.println("***********************************************************************");
				System.out.println("**********很遗憾,本文没有内容,请从新选择,5秒后为您跳转.......");
				System.out.println("***********************************************************************");
				Thread.sleep(4500);
			}
			for(int i=0;i<page-1;i++)
			{
				System.out.println("***********************************************************************");

				String view = content.substring(i*300,i*300+300);//new String(novelContent,i*300,300);
				System.out.println(view);
				if(i==page-2)		//这是倒数第二页 那就全部输出来吧
				{
					String view2 = content.substring(i*300+300,i*300+300+endPageByte);//new String(novelContent,i*300+300,endPageByte);
					System.out.println(view2);
				}
				Scanner choose5 = new Scanner(System.in);
				System.out.println("***********************************************************************");
				System.out.println("-----------0:退回上级界面---------------1：上一页 2:下一页---------------");
				while(true)
				{
					System.out.println("输入您的选择 ：");
					String cs_5 = choose5.next();	
					if(!CheckIsNumber(cs_5))			//真为数字，假为字母
					{
						System.out.println("请您输入数字");	continue;							//非数字执行这条
					}
					else if("0".equals(cs_5))
					{
						System.out.println("正在为您返回上级菜单.......");return false;
					}
					else if("1".equals(cs_5)&&i==0)
					{
						System.out.println("当前已是第一页,不能执行上一页操作."); continue;
					}
					else if("1".equals(cs_5))	//1就是想看上一页嘛
					{
						i=i-2; break;
					}
					else if("2".equals(cs_5)&&i==page-2)
					{
						System.out.println("当前已是最后一页,不能执行上一页操作.返回上级菜单请输入0"); continue;
					}
					else if("2".equals(cs_5))break;	//2是想看下一页
					else
					{
						System.out.println("请您输入0,1,2 不要输入其他数字 ");	continue;
					}
				}
			}
			return false;
	}
	/*	
	 * 登陆模块	(完结)
	 * 返回true表示登录成功,返回false表示登录失败
	 * 可以过滤输入
	 * 输入无效后可选择退出界面或继续输入
	 */
	static boolean logIn() throws IOException	
	{
		System.out.println("---------------------------------");
		System.out.println("请输入您的账号：");
		Scanner scanner = new Scanner(System.in); 
		String account = scanner.next().trim();
		while(!(account!=null&&account!=""))
		{
			System.out.println("请重新输入有效的账号,返回菜单请输入110");
			account = scanner.next().trim();
			if("110".equals(account))
			{
				return false;
			}
		}
		System.out.println("请输入您的密码：");
		String password = scanner.next().trim();
		while(!(password!=null&&password!=""))
		{
			System.out.println("请重新输入有效的密码,返回菜单请输入120");
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
			System.out.println("账号密码正确，登录成功");
			return true;
		}
		else
		{
			System.out.println("账号密码错误，登录失败");return false;
		}
	}

	/*
	 * 注册模块(完结)
	 */
	static boolean enrol() throws IOException						//注册
	{
		System.out.println("----------欢迎注册--------------");		//为什么要放外面?欢迎一次就够了
		while(true)
		{
			System.out.println("请设置您的账号,长度必须小于10位");
			Scanner scanner = new Scanner(System.in);
			String account = scanner.next();					//设置账号
			if(account.length()>=10)
			{
				System.out.println("********************************");
				System.out.println("您输入的账号长于或等于10位,请从新输入");
				continue;
			}
			dos.writeInt(Op.accounIn);
			dos.writeUTF(account);
			dos.flush();
			if(dis.readBoolean())
			{
				System.out.println("此帐号已被注册请您从新注册");
				continue;
			}
			String password="";
			while(true)
			{
				System.out.println("请设置您的密码,密码小于15位");
				password = scanner.next();				//设置密码
				if(password.length()>=15)
				{
					System.out.println("********************************");
					System.out.println("您输入的密码长于或者等于15位,请从新输入");
					continue;
				}
				break;
			}
			System.out.println("正在为您注册,请稍等片刻...");
			dos.writeInt(Op.LOGIN);
			dos.writeUTF(account);
			dos.writeUTF(password);
			dos.flush();
			if(dis.readBoolean())
			{
				System.out.println("恭喜你,注册成功了");
				return true;
			}
			System.out.println("非常遗憾,注册失败了");
		}	
	}
}
//*********************************************This is my fist project*********************************************************************
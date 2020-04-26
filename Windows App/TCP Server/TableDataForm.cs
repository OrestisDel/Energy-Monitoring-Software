using System;
using System.Data;
using System.Windows.Forms;
using MySql.Data.MySqlClient;
using Excel = Microsoft.Office.Interop.Excel;
using System.IO;

namespace login
{
    
    public partial class frmtabledata : System.Windows.Forms.Form
    {
       
        public string strD { get; set; }      

        public string strD2 { get; set; }

        
        public bool check;

        public frmtabledata()
        {
            InitializeComponent();         
        }

        private void Datagridview_to_txt_file()
        {
            try
            {
				//Enable Clipboard to include Header Text 
                dataGridView1.ClipboardCopyMode = DataGridViewClipboardCopyMode.EnableAlwaysIncludeHeaderText;
                dataGridView1.SelectAll();

                //hiding row headers to avoid extra \t in exported text
                var rowHeaders = dataGridView1.RowHeadersVisible;
                dataGridView1.RowHeadersVisible = false;

                // ! creating text from grid values
                string content =dataGridView1.GetClipboardContent().GetText();

                // restoring grid state
                dataGridView1.ClearSelection();
                dataGridView1.RowHeadersVisible = rowHeaders;

                File.WriteAllText(@"C:\Users\Orestis\Desktop\Test\Exprt_txt.txt", content);
            }catch(Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            } 
        }

        private void frmtabledata_FormClosing(object sender, FormClosingEventArgs e)
        {
            Application.Exit();
        }

        private void Frmtabledata_Load(object sender, EventArgs e)
        {
            this.BindGrid();

            label1.Text = strD;
            label2.Text = strD2;
            checkBox1.Checked = check;
        }
        private void BindGrid()
        {
		       	//connects to the specified MySql database
            string conString = @"server = 127.0.0.1; user id = ******; password=*********; database=power;";
            using (MySqlConnection con = new MySqlConnection(conString))
            {
					           //selects rows and columns from table 'consumption'
                    using (MySqlCommand cmd = new MySqlCommand("SELECT * FROM consumption ", con))
                    {
                        cmd.Connection.Open();

                    try
                    {
					            	//updates dataset executing cmd
                        MySqlDataAdapter sda = new MySqlDataAdapter(cmd);
                        DataTable dt = new DataTable();
						            //specifies that the cmd must be interpreted as SQL text command
                        cmd.CommandType = CommandType.Text;
                        using (sda)
                        {
                            using (dt)
                            {
							                 	//updates the dataset to match the datatable
                                sda.Fill(dt);
								                //sets datatable dt as the source for the dataGridView1
                                dataGridView1.DataSource = dt;
								                 //creates a chart with dataGridView1 as the source. Values of column position 0 go on X-Axis and values of column position 2 go on Y-Axis
                                chart1.Series["kWh/30minutes"].XValueMember = dataGridView1.Columns[0].DataPropertyName;
                                chart1.Series["kWh/30minutes"].YValueMembers = dataGridView1.Columns[2].DataPropertyName;
                                chart1.DataSource = dataGridView1.DataSource;
                            }
                        }
                        if (!check==true)
                        {
							              //if boolean check is false then dataGridView1 filters out the values outside the set indicated by strings strD and strD2
                            (dataGridView1.DataSource as DataTable).DefaultView.RowFilter = "Date >='" + strD + "' AND Date <='" + strD2 + "'";
                            label1.Visible = true;
                            label2.Visible = true;
                            label3.Visible = true;
                        }
                        else{ }
                    }

                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.Message);
                    }
                        cmd.Connection.Close();
                    }
                try
                {
					          //calls the method to copy dataGridView1 to a text file
                    Datagridview_to_txt_file();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
        }
               
        private void copyAlltoClipboard()
        {
			      //Copies dataGridView1 to clipboard including headers
            dataGridView1.ClipboardCopyMode = DataGridViewClipboardCopyMode.EnableAlwaysIncludeHeaderText;
            dataGridView1.SelectAll();
            DataObject dataObj = dataGridView1.GetClipboardContent();
            if (dataObj != null)
                Clipboard.SetDataObject(dataObj);
        }

        private void ButtonExcel_Click(object sender, EventArgs e)
        {
            copyAlltoClipboard();
            Microsoft.Office.Interop.Excel.Application xlexcel;
            Microsoft.Office.Interop.Excel.Workbook xlWorkBook;
            Microsoft.Office.Interop.Excel.Worksheet xlWorkSheet;
            object misValue = System.Reflection.Missing.Value;
			      //initializes a new excel application
            xlexcel = new Excel.Application();
            xlexcel.Visible = true;
			      //returns an empty workbook
            xlWorkBook = xlexcel.Workbooks.Add(misValue);
			      //gets worksheet number 1
            xlWorkSheet = (Excel.Worksheet)xlWorkBook.Worksheets.get_Item(1);
            Excel.Range CR = (Excel.Range)xlWorkSheet.Cells[1, 1];
			      //selects worksheet cell with coordinates row 1 column 1
            CR.Select();
			      //pastes the contents of the clipboard on the selected range
            xlWorkSheet.PasteSpecial(CR, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, true);
        }
    }
}

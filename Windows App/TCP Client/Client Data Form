using System;
using System.Data;
using System.Linq;
using System.Windows.Forms;
using System.IO;
using Excel = Microsoft.Office.Interop.Excel;

namespace TCP_Client
{
    public partial class ClientData : Form
    {
        string filepath = @"C:\Users\Orestis\source\repos\login\TCP Client\bin\DebugExprt_txt.txt";
        string fileContent = "";
        string filename = "DebugExprt_txt.txt";

        public ClientData()
        {
            InitializeComponent();
			//checks if file exist on the specified directory. if true then calls the DataTableFromTextFile method from the Helper class
            bool fileExist = File.Exists(filepath);
            if (fileExist)
            {
                dataGridView1.DataSource = Helper.DataTableFromTextFile(filepath);
				//calls the chart function
                Chart();
            }
            else
            {
                MessageBox.Show("File does not exist");
            }           
        }
		
        private void ClientData_Load(object sender, EventArgs e)
        {

        }

        private void Chart()
        {
            //creates a chart with dataGridView1 as the data source
            chart1.Series["kWh/30 minutes"].XValueMember = dataGridView1.Columns[0].DataPropertyName;
            chart1.Series["kWh/30 minutes"].YValueMembers = dataGridView1.Columns[2].DataPropertyName;
            chart1.DataSource = dataGridView1.DataSource;
        }

        private void button1_Click(object sender, EventArgs e)
        {
			//filters dataGridView1 according to the dateTimePicker1 and dateTimePicker2 values and calls SumMinMax() 
            (dataGridView1.DataSource as DataTable).DefaultView.RowFilter = "Date >='" + dateTimePicker1.Text + "' AND Date <='" + dateTimePicker2.Text + "'";
            SumMinMax();
        }

        private void SumMinMax()
        {
           //selects rows form column[2] and converts them to a long[] array
           long[] columnData = (from DataGridViewRow row in dataGridView1.Rows
                                where (Convert.ToInt64(row.Cells[2].Value) != 0)
                                select Convert.ToInt64(row.Cells[2].Value)).ToArray();
			//performs sum, min, max and average calculations and display the results to the specidifed text boxes
            txtSum.Text = columnData.Sum().ToString();

            txtMax.Text = columnData.Max().ToString();

            txtMin.Text = columnData.Min().ToString();

            txtAvg.Text = columnData.Average().ToString();
			//calls for GetMedian()
            txtMedian.Text = GetMedian(columnData).ToString();

           //calls for multiplyCost()
            multiplyCost();
        }
        private void multiplyCost()
        {
			//calculates cost multiplying the tariff by the sum
            float Tariff = float.Parse(txtTarrif.Text);
            int total = int.Parse(txtSum.Text);
            float Cost = (Tariff * total ) / 100;
            txtCost.Text = Cost.ToString();
        }

        public static long GetMedian(long[] sourceNumbers)
        {
            if(sourceNumbers == null || sourceNumbers.Length == 0)
            {
                throw new System.Exception("Median of empty array not defined.");
            }
            else{
                //sorts the list
                long[] sortedPNumbers = (long[])sourceNumbers.Clone();
                Array.Sort(sortedPNumbers);

                //gets the median
                int size = sortedPNumbers.Length;
                int mid = size / 2;
                long median = (size % 2 != 0) ? (long)sortedPNumbers[mid] :
                    ((long)sortedPNumbers[mid] + (long)sortedPNumbers[mid - 1]) / 2;
                return median;
            }           
        }

        private void copyAlltoClipboard()
        {
			//copies dataGridView1 and headers to clipboard 
            dataGridView1.ClipboardCopyMode = DataGridViewClipboardCopyMode.EnableAlwaysIncludeHeaderText;
            dataGridView1.SelectAll();
            DataObject dataObj = dataGridView1.GetClipboardContent();
            if (dataObj != null)
                Clipboard.SetDataObject(dataObj);
        }

        private void button2_Click_1(object sender, EventArgs e)
        {          
            if (dataGridView1.Rows.Count > 1 && dataGridView1.Rows != null)
            {
                SumMinMax();
            }
        }

        private void btnExport_Click(object sender, EventArgs e)
        {
            copyAlltoClipboard();
            Microsoft.Office.Interop.Excel.Application xlexcel;
            Microsoft.Office.Interop.Excel.Workbook xlWorkBook;
            Microsoft.Office.Interop.Excel.Worksheet xlWorkSheet;
            object misValue = System.Reflection.Missing.Value;
            xlexcel = new Excel.Application();
            xlexcel.Visible = true;
            xlWorkBook = xlexcel.Workbooks.Add(misValue);
            xlWorkSheet = (Excel.Worksheet)xlWorkBook.Worksheets.get_Item(1);

            //Imports Labels 
            ((Excel.Range)xlWorkSheet.Cells[1, 1]).Value = this.label2.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 1]).Value = this.label3.Text;
            ((Excel.Range)xlWorkSheet.Cells[3, 1]).Value = this.label9.Text;
            ((Excel.Range)xlWorkSheet.Cells[1, 4]).Value = this.label4.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 4]).Value = this.label5.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 7]).Value = this.label7.Text;
            ((Excel.Range)xlWorkSheet.Cells[1, 7]).Value = this.label6.Text;

            //Imports TextBox values
            ((Excel.Range)xlWorkSheet.Cells[1, 2]).Value = this.txtSum.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 2]).Value = this.txtAvg.Text;
            ((Excel.Range)xlWorkSheet.Cells[3, 2]).Value = this.txtMedian.Text;
            ((Excel.Range)xlWorkSheet.Cells[1, 5]).Value = this.txtMin.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 5]).Value = this.txtMax.Text;
            ((Excel.Range)xlWorkSheet.Cells[2, 8]).Value = this.txtCost.Text;
            ((Excel.Range)xlWorkSheet.Cells[1, 8]).Value = this.txtTarrif.Text;

            //Pastes Grid to excel worksheet
            Excel.Range CR = (Excel.Range)xlWorkSheet.Cells[5, 1];
            CR.Select();
            xlWorkSheet.PasteSpecial(CR, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, true);
        }
    }

    class Helper
        {
            public static string filepath = @"C:\Users\Orestis\source\repos\login\TCP Client\bin\DebugExprt_txt.txt";
            public static string filename = "Exprt_txt.txt";

            public static DataTable DataTableFromTextFile(string location, char delimiter = '\t')
            {
                DataTable result;
                location = filepath;
				//reads the specified file line by line and inputs resulted string in FromDataTable()
                string[] LineArray = File.ReadAllLines(location);
				//calls FromDataTable()
                result = FromDataTable(LineArray, delimiter);
                return result;
            }
            private static DataTable FromDataTable(string[] LineArray, char delimiter)
            {
                DataTable dt = new DataTable();
				//calls the AddColumnToTable and AddRowToTable methods
                AddColumnToTable(LineArray, delimiter, ref dt);
                AddRowToTable(LineArray, delimiter, ref dt);

                return dt;
            }

            private static void AddRowToTable(string[] valueCollection, char delimiter, ref DataTable dt)
            {
				//loops for the length of the LineArray
                for (int i = 0; i < valueCollection.Length; i++)
                {
					//splits the string using the delimiter specified as 'tab-whitespace'
                    string[] values = valueCollection[i].Split(delimiter);
                    //creates a new row
                    DataRow dr = dt.NewRow();
					//loops for the number of values derived after splitting
                    for (int j = 0; j < values.Length; j++)
                    {
                        dr[j] = values[j];
                    }
						//adds the values to rows
                         dt.Rows.Add(dr);                
                }
            }

            private static void AddColumnToTable(string[] columnCollectio, char delimiter, ref DataTable dt)
            {
				//splits the string using the delimiter specified as 'tab-whitespace'
                string[] columns = columnCollectio[0].Split(delimiter);
				//loops for each string derived from the splitting
                foreach (string columnName in columns)
                {
					//represents the schema of a column in a datatable
                    DataColumn dc = new DataColumn(columnName, typeof(string));
					//adds the specified data column object to the data column collection
                    dt.Columns.Add(dc);
                }
            }      
		}
}
